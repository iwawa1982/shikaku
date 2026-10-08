
package com.example.demo.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * 資格試験の選択肢情報を管理するクラス。
 * PostgreSQLのchoicesテーブルに対応する。
 */
@Data
public class Choice {

    // 選択肢ID（主キー）
    private Integer id;

    // この選択肢が属する問題のID
    private Integer questionId;

    // 選択肢番号（1～4）
    private Integer choiceNo;

    // 選択肢の文章（必須）
    @NotBlank(message = "選択肢の文章を入力してください")
    private String choiceText;

    // 選択肢の記述が正しいかどうか（必須）
    // true：正しい（〇）、false：誤り（×）
    @NotNull(message = "記述の正誤を選択してください")
    private Boolean isStatementCorrect;

    // 選択肢ごとの解説（任意）
    private String choiceExplanation;
}
