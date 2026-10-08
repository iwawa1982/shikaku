
package com.example.demo.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Choice;
import com.example.demo.entity.Question;
import com.example.demo.form.QuestionForm;
import com.example.demo.repository.QuestionMapper;
import com.example.demo.service.QuestionService;

/**
 * 資格試験の問題一覧・登録画面を管理するController。
 */
@Controller
public class QuestionController {

    // 問題データを取得するMapper
    private final QuestionMapper questionMapper;

    // 問題登録処理を担当するService
    private final QuestionService questionService;

    /**
     * コンストラクタ
     */
    public QuestionController(
            QuestionMapper questionMapper,
            QuestionService questionService) {

        this.questionMapper = questionMapper;
        this.questionService = questionService;
    }

    /**
     * 問題一覧画面を表示する。
     */
    @GetMapping("/questions")
    public String list(Model model) {

        // データベースから問題一覧を取得
        List<Question> questions = questionMapper.findAll();

        // HTMLに問題一覧を渡す
        model.addAttribute("questions", questions);

        return "questions/list";
    }

    /**
     * 問題登録画面を表示する。
     */
    @GetMapping("/questions/new")
    public String newQuestion(Model model) {

        // 空の入力フォームを作成
        QuestionForm form = new QuestionForm();

        // HTMLにフォームを渡す
        model.addAttribute("questionForm", form);

        return "questions/form";
    }

    /**
     * 問題と選択肢をPostgreSQLに登録する。
     */
    @PostMapping("/questions")
    public String create(
            @Valid @ModelAttribute("questionForm") QuestionForm questionForm,
            BindingResult bindingResult) {

        // 入力チェックでエラーがあれば登録画面に戻る
        if (bindingResult.hasErrors()) {
            return "questions/form";
        }

        // 入力された情報からQuestionを作成
        Question question = new Question();

        question.setQualificationId(
                questionForm.getQualificationId());

        question.setCategoryId(
                questionForm.getCategoryId());

        question.setYear(
                questionForm.getYear());

        question.setQuestionNo(
                questionForm.getQuestionNo());

        question.setQuestionText(
                questionForm.getQuestionText());

        question.setCorrectChoiceNo(
                questionForm.getCorrectChoiceNo());

        question.setExplanation(
                questionForm.getExplanation());

        // 入力された4件の選択肢を取得
        List<Choice> choices = questionForm.getChoices();

        // Serviceを呼び出してデータベースに登録
        questionService.registerQuestion(question, choices);

        // 登録後は問題一覧画面へ移動
        return "redirect:/questions";
    }
}
