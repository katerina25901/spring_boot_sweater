package com.example.sweater.controller;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
            saveFile(message, file);
            model.addAttribute("message", null);
            messageRepo.save(message);
        }
        Iterable<Message> messages =  messageRepo.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }


    //метод принимает текущего пользователя, которго берет из сесии, а еще
//    будет смотреть, какого пользовтеля мы запрашиваем, для этого мы проверяем @PathVariable User user
    @GetMapping("/user-messages/{user}")
    public String userMessages(
            @AuthenticationPrincipal User currentUser,
//            если переменная отличалась бы от наименования user
//            то мы указали бы так   @PathVariable(name = "user") User myUser
            @PathVariable User user,
            Model model,
//            чтобы спринг автоматически брал RequestParam из getзапроса message и
//            находил объект в базе данных и ложил в переменную message
            @RequestParam(required = false) Message message
    ) {
        Set<Message> messages = user.getMessages();
        //добавляем параметры, которые получает userMessages.ftl
        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        model.addAttribute("messages",messages);
//        заинжектим message в нашу модель
        model.addAttribute("message", message);
        model.addAttribute("isCurrentUser",currentUser.equals(user));


        return "userMessages";
    }

    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long user,
            @RequestParam("id") Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam ("file") MultipartFile file
    ) throws IOException {
        if (message.getAuthor().equals(currentUser)) {
            if (!StringUtils.isEmpty(text)) {
                message.setText(text);
            }
            if (!StringUtils.isEmpty(tag)) {
                message.setTag(tag);
            }
            saveFile(message, file);
            messageRepo.save(message);
        }
        return  "redirect:/user-messages/" + user;

    }

    private void saveFile(@Valid Message message, @RequestParam("file") MultipartFile file) throws IOException {
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
    }

}
