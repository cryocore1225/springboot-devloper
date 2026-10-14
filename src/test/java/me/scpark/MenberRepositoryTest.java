package me.scpark;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class MenberRepositoryTest {
    @Autowired


    private MenberRepository menberRepository;
    @Sql("/insert-members.sql")

    @DisplayName("MemberRepository를 통해 member 테이블의 모든 레코드(3개) 가져오기.")


    @Test
    public void  getAllMenbers(){

        List<Menber> menbers=menberRepository.findAll();

        assertThat(menbers.size()).isEqualTo(3);
    }
    @DisplayName("MemberRepository를 통해 이름이 'C'인 레코드 가져오기")
    @Sql("/insert-members.sql")
    @Test
    public void getMemberByName() {
        // 준비 given

        // 실행 when : select * from member where name =?
        Optional<Menber> optMember = menberRepository.findByName("C");
        Menber menber = optMember.get();

        // 검증 then
        assertThat(menber.getid()).isEqualTo(3);
    }



    @DisplayName("MemberRepository를 통해 id이 2인 레코드 가져오기")
    @Sql("/insert-members.sql")
    @Test
    public void getMemberByid(){
        //given



        // when
        Optional<Menber> optMember = menberRepository.findById(2L);
        Menber member = optMember.get();

        // then
        assertThat(member.getName()).isEqualTo("B");
    }

    @DisplayName("MemberRepository를 통해 이름이 '박성철'인 레코드 추가하기")
    @Test
    public void saveMenber() {
        // 준비 given
        Menber menber = new Menber("박성철");

        // 실행 when
        menberRepository.save(menber);

        // 검증 then
        List<Menber> result = menberRepository.findAll();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("박성철");
    }
}