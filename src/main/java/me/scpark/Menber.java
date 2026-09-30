package me.scpark;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class Menber{


     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name="id",nullable = false)
    private Long id;
     @Column(name="name",nullable = false)
    private String   name;

     public String getName(){
         return  name;
     }



}
