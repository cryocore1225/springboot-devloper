package me.scpark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenberService {
    @Autowired
    private MenberRepository menberRepository;

    public List<Menber> getAllMenbers(){
        return menberRepository.findAll();


    }
}
