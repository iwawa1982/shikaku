
package com.example.demo.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 解答履歴を管理するクラス。
 * answer_historyテーブルに対応する。
 */
@Data
public class AnswerHistory {

    // 解答履歴のID
    private Integer id;

    // 解答した問題のID
    private Integer questionId;

    // 選択した選択肢のID
    private Integer choiceId;

    // 解答形式（FOUR_CHOICE / TRUE_FALSE）
    private String mode;

    // ○×問題で選択した解答
    // 4択問題では使用しないためnull
    private Boolean userAnswer;

    // 正解したかどうか
    private Boolean isCorrect;

    // 解答した日時
    private LocalDateTime answeredAt;
}
