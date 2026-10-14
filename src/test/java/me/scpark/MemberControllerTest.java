package me.scpark;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
@SpringBootTest
   @AutoConfigureMockMvc

class MemberControllerTest {

    @Autowired
    private MenberRepository menberRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void cleanup(){
        menberRepository.deleteAll();
    }

    @DisplayName("TEST")
    @Test
    void getAllMembers() throws Exception {
        //(given)

        Menber m=new Menber(null,"SCPARK");
        Menber saveMenber=menberRepository.save(m);




        //(when)
        ResultActions result = mockMvc.perform(get("/member")
                .accept(MediaType.APPLICATION_JSON));



        //(then)
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0],id").value(saveMenber.getid()))
                .andExpect(jsonPath("$[0],name").value(saveMenber.getName()));
    }
}