
package com.example.demo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.demo.entity.AnswerHistory;

/**
 * 解答履歴テーブルにアクセスするMapper。
 * 解答履歴の登録・取得を担当する。
 */
@Mapper
public interface AnswerHistoryMapper {

    /**
     * 解答履歴を1件登録する。
     *
     * @param history 登録する解答履歴
     * @return 登録された行数（通常は1）
     */
    @Insert("""
            INSERT INTO answer_history (
                question_id,
                choice_id,
                mode,
                user_answer,
                is_correct,
                answered_at
            )
            VALUES (
                #{questionId},
                #{choiceId},
                #{mode},
                #{userAnswer},
                #{isCorrect},
                CURRENT_TIMESTAMP
            )
            """)
    int insert(AnswerHistory history);

    /**
     * 指定した問題の直近3回の4択解答履歴を取得する。
     *
     * @param questionId 問題ID
     * @return 新しい順に並んだ解答履歴（最大3件）
     */
    @Select("""
            SELECT
                id,
                question_id AS questionId,
                choice_id AS choiceId,
                mode,
                user_answer AS userAnswer,
                is_correct AS isCorrect,
                answered_at AS answeredAt
            FROM answer_history
            WHERE question_id = #{questionId}
              AND mode = 'FOUR_CHOICE'
            ORDER BY answered_at DESC, id DESC
            LIMIT 3
            """)
    List<AnswerHistory> findLatestThreeByQuestionId(
            Integer questionId);
}
