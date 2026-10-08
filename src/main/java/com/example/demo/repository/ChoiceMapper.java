
package com.example.demo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.demo.entity.Choice;

/**
 * choicesテーブルにアクセスするためのMapper。
 * 選択肢の登録・取得を担当する。
 */
@Mapper
public interface ChoiceMapper {

    /**
     * 選択肢を1件登録する。
     *
     * @param choice 登録する選択肢情報
     * @return 登録された行数（通常は1）
     */
    @Insert("""
            INSERT INTO choices (
                question_id,
                choice_no,
                choice_text,
                is_statement_correct,
                choice_explanation
            )
            VALUES (
                #{questionId},
                #{choiceNo},
                #{choiceText},
                #{isStatementCorrect},
                #{choiceExplanation}
            )
            """)
    int insert(Choice choice);

    /**
     * 指定した問題IDに属する選択肢を取得する。
     * 選択肢番号の昇順（1～4）で取得する。
     *
     * @param questionId 問題ID
     * @return 選択肢の一覧
     */
    @Select("""
            SELECT
                id,
                question_id AS questionId,
                choice_no AS choiceNo,
                choice_text AS choiceText,
                is_statement_correct AS isStatementCorrect,
                choice_explanation AS choiceExplanation
            FROM choices
            WHERE question_id = #{questionId}
            ORDER BY choice_no ASC
            """)
    List<Choice> findByQuestionId(Integer questionId);
}
