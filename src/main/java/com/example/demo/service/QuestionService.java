
package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Choice;
import com.example.demo.entity.Question;
import com.example.demo.repository.ChoiceMapper;
import com.example.demo.repository.QuestionMapper;

/**
 * 資格試験の問題登録に関する処理を担当するService。
 */
@Service
public class QuestionService {

    // 問題テーブルを操作するMapper
    private final QuestionMapper questionMapper;

    // 選択肢テーブルを操作するMapper
    private final ChoiceMapper choiceMapper;

    /**
     * コンストラクタ
     * Spring Bootが各Mapperを自動的に渡す。
     */
    public QuestionService(
            QuestionMapper questionMapper,
            ChoiceMapper choiceMapper) {

        this.questionMapper = questionMapper;
        this.choiceMapper = choiceMapper;
    }

    /**
     * 問題1件と選択肢4件をまとめて登録する。
     *
     * @param question 登録する問題
     * @param choices 登録する4つの選択肢
     */
    @Transactional
    public void registerQuestion(
            Question question,
            List<Choice> choices) {

        // 選択肢が4件あるか確認する
        if (choices == null || choices.size() != 4) {
            throw new IllegalArgumentException(
                    "選択肢は4件必要です。");
        }

        // 問題を登録する
        questionMapper.insert(question);

        // 登録された問題のIDを取得する
        Integer questionId = question.getId();

        // 4つの選択肢を順番に登録する
        for (int i = 0; i < choices.size(); i++) {

            Choice choice = choices.get(i);

            // 登録した問題のIDを設定
            choice.setQuestionId(questionId);

            // 選択肢番号を1～4に設定
            choice.setChoiceNo(i + 1);

            // 選択肢を登録
            choiceMapper.insert(choice);
        }
    }
}
