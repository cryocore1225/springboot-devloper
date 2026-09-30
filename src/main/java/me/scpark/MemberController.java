package me.scpark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MemberController {
    @Autowired
    private MenberService menberService;
    @GetMapping("/menber")
    public List<Menber> getAllMembers(){
       return menberService.getAllMenbers();
    }
}
