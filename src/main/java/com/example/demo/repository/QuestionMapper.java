
package com.example.demo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.example.demo.entity.Question;

/**
 * questionsテーブルにアクセスするためのMapper。
 * 問題の取得・登録を担当する。
 */
@Mapper
public interface QuestionMapper {

    /**
     * 登録されている問題をすべて取得する。
     *
     * @return 問題の一覧
     */
    @Select("""
            SELECT
                id,
                qualification_id AS qualificationId,
                category_id AS categoryId,
                year,
                question_no AS questionNo,
                question_text AS questionText,
                correct_choice_no AS correctChoiceNo,
                explanation
            FROM questions
            ORDER BY year DESC, question_no ASC
            """)
    List<Question> findAll();

    /**
     * 新しい問題を登録する。
     *
     * @param question 登録する問題情報
     * @return 登録された行数（通常は1）
     */
    @Insert("""
            INSERT INTO questions (
                qualification_id,
                category_id,
                year,
                question_no,
                question_text,
                correct_choice_no,
                explanation
            )
            VALUES (
                #{qualificationId},
                #{categoryId},
                #{year},
                #{questionNo},
                #{questionText},
                #{correctChoiceNo},
                #{explanation}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Question question);
}
