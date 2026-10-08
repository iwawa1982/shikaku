package com.example.demo.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DatabaseTestMapper {

    @Select("SELECT COUNT(*) FROM questions")
    int countQuestions();
}