package com.example.sweater.controller;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.repos.MessageRepo;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.sun.org.apache.xml.internal.serializer.utils.Utils.messages;

@Controller
public class MainController {
    @Autowired
    private MessageRepo messageRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter, Model model) {
        Iterable<Message> messages =  messageRepo.findAll();
        if (filter !=null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);}
        else{
            messages = messageRepo.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);

        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            // BindingResult это список агрументов и сообщений ошибок валидации, тут есть такая тонкость,
//    что данный аргумент всегда должен идти перед аргументом Model
//    (Map<String, Object> model тут не проходит, нгужно ), если нарушить порядок
//                    (т.е. сначала модел, потом банднгрезалт, то все ошибки будут сыпаться во вью
//                    без всякой обработки)
//    ->
            BindingResult bindingResult,
            Model model,
            @RequestParam ("file") MultipartFile file
    ) throws IOException {
//        Message message = new Message(text, tag, user);
//        заменяем на  message.setAuthor(user), т.к. валидацию добавили в класс Messege вот эту  :
//        @NotBlank(message = "Please fill the message")
//        @Length(max = 2048, message = "Message too long (more than 2kB)")
//        И добавляем аанотацию Valid
        message.setAuthor(user);
//получаем ошибки из bindingResult
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        } else {
            if (file != null && !file.getOriginalFilename().isEmpty()) {
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                String uuidFile = UUID.randomUUID().toString();
                String resultFilename = uuidFile + "." + file.getOriginalFilename();
                file.transferTo(new File(uploadPath + "/" + resultFilename));
                message.setFilename(resultFilename);

            }
            model.addAttribute("message", null);
            messageRepo.save(message);
        }
        Iterable<Message> messages =  messageRepo.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }


}
