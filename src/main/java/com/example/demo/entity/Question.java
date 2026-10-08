package com.example.demo.entity;

import lombok.Data;

/**
 * 資格試験の問題情報を管理するクラス。
 * questionsテーブルに対応する。
 */
@Data
public class Question {

    // 問題ID
    private Integer id;

    // 資格ID
    private Integer qualificationId;

    // 分野ID
    private Integer categoryId;

    // 出題年度
    private Integer year;

    // 問題番号
    private Integer questionNo;

    // 問題文
    private String questionText;

    // 4択問題の正解番号
    private Integer correctChoiceNo;

    // 問題全体の解説
    private String explanation;
}