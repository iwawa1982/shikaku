
package com.example.demo.form;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.demo.entity.Choice;

import lombok.Data;

/**
 * 問題登録画面から入力された情報を受け取るクラス。
 */
@Data
public class QuestionForm {

    // 資格ID（必須）
    @NotNull(message = "資格を選択してください")
    private Integer qualificationId;

    // 分野ID（必須）
    @NotNull(message = "分野を選択してください")
    private Integer categoryId;

    // 出題年度（必須）
    @NotNull(message = "出題年度を入力してください")
    private Integer year;

    // 問題番号（必須）
    @NotNull(message = "問題番号を入力してください")
    @Min(value = 1, message = "問題番号は1以上にしてください")
    private Integer questionNo;

    // 問題文（必須）
    @NotBlank(message = "問題文を入力してください")
    private String questionText;

    // 4択問題の正解番号（1～4）
    @NotNull(message = "正解番号を選択してください")
    @Min(value = 1, message = "正解番号は1～4です")
    @Max(value = 4, message = "正解番号は1～4です")
    private Integer correctChoiceNo;

    // 問題全体の解説（任意）
    private String explanation;

    // 選択肢1～4
    private List<Choice> choices = new ArrayList<>();

    /**
     * 初期表示時に4つの選択肢を用意する。
     */
    public QuestionForm() {

        for (int i = 0; i < 4; i++) {

            Choice choice = new Choice();

            // 選択肢番号を1～4に設定
            choice.setChoiceNo(i + 1);

            choices.add(choice);
        }
    }
}
