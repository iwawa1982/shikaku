
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

    /**
     * 問題IDを指定して、問題を1件取得する。
     *
     * @param id 問題ID
     * @return 指定された問題
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
            WHERE id = #{id}
            """)
    Question findById(Integer id);

    /**
     * 直近の4択解答が不正解だった問題を取得する。
     *
     * 各問題の最新の解答履歴を調べ、
     * 不正解だった問題だけを取得する。
     *
     * @return 復習対象の問題一覧
     */
    @Select("""
            SELECT
                q.id,
                q.qualification_id AS qualificationId,
                q.category_id AS categoryId,
                q.year,
                q.question_no AS questionNo,
                q.question_text AS questionText,
                q.correct_choice_no AS correctChoiceNo,
                q.explanation
            FROM questions q
            INNER JOIN LATERAL (
                SELECT
                    ah.is_correct
                FROM answer_history ah
                WHERE ah.question_id = q.id
                  AND ah.mode = 'FOUR_CHOICE'
                ORDER BY ah.answered_at DESC, ah.id DESC
                LIMIT 1
            ) latest ON true
            WHERE latest.is_correct = false
            ORDER BY q.year DESC, q.question_no ASC
            """)
    List<Question> findQuestionsToReview();

}
