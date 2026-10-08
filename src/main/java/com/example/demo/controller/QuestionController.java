
package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.AnswerHistory;
import com.example.demo.entity.Choice;
import com.example.demo.entity.Question;
import com.example.demo.form.QuestionForm;
import com.example.demo.repository.AnswerHistoryMapper;
import com.example.demo.repository.ChoiceMapper;
import com.example.demo.repository.QuestionMapper;
import com.example.demo.service.QuestionService;

/**
 * 資格試験の問題を管理するController。
 *
 * 主な機能：
 * ・問題一覧の表示
 * ・問題の新規登録
 * ・4択問題の表示と採点
 * ・解答履歴の保存
 * ・直近3回の解答履歴の取得
 * ・復習問題一覧の表示
 * ・○×問題の表示と採点
 */
@Controller
public class QuestionController {

    // 問題データを取得・登録するMapper
    private final QuestionMapper questionMapper;

    // 選択肢データを取得するMapper
    private final ChoiceMapper choiceMapper;

    // 問題登録処理を担当するService
    private final QuestionService questionService;

    // 解答履歴を保存・取得するMapper
    private final AnswerHistoryMapper answerHistoryMapper;

    /**
     * コンストラクタ
     *
     * Spring Bootが必要なオブジェクトを渡す。
     */
    public QuestionController(
            QuestionMapper questionMapper,
            ChoiceMapper choiceMapper,
            QuestionService questionService,
            AnswerHistoryMapper answerHistoryMapper) {

        this.questionMapper = questionMapper;
        this.choiceMapper = choiceMapper;
        this.questionService = questionService;
        this.answerHistoryMapper = answerHistoryMapper;
    }

    /**
     * 問題一覧画面を表示する。
     *
     * 各問題の直近3回の4択解答履歴も取得する。
     *
     * GET /questions
     */
    @GetMapping("/questions")
    public String list(Model model) {

        // 登録済みの問題を取得
        List<Question> questions = questionMapper.findAll();

        // 問題IDごとに解答履歴を管理するMap
        Map<Integer, List<AnswerHistory>> historyMap =
                new HashMap<>();

        // 問題ごとの直近3回の解答履歴を取得
        for (Question question : questions) {

            List<AnswerHistory> histories =
                    answerHistoryMapper.findLatestThreeByQuestionId(
                            question.getId());

            historyMap.put(question.getId(), histories);
        }

        // HTMLへデータを渡す
        model.addAttribute("questions", questions);
        model.addAttribute("historyMap", historyMap);

        return "questions/list";
    }

    /**
     * 問題登録画面を表示する。
     *
     * GET /questions/new
     */
    @GetMapping("/questions/new")
    public String newQuestion(Model model) {

        // 空の入力フォームを作成
        QuestionForm form = new QuestionForm();

        model.addAttribute("questionForm", form);

        return "questions/form";
    }

    /**
     * 問題と4件の選択肢を登録する。
     *
     * POST /questions
     */
    @PostMapping("/questions")
    public String create(
            @Valid @ModelAttribute("questionForm") QuestionForm questionForm,
            BindingResult bindingResult) {

        // 入力チェックでエラーがあれば登録画面へ戻る
        if (bindingResult.hasErrors()) {
            return "questions/form";
        }

        // フォームの入力内容からQuestionを作成
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

        // 選択肢4件を取得
        List<Choice> choices = questionForm.getChoices();

        // 問題と選択肢を登録
        questionService.registerQuestion(question, choices);

        return "redirect:/questions";
    }

    /**
     * 4択問題の解答画面を表示する。
     *
     * GET /questions/{id}
     */
    @GetMapping("/questions/{id}")
    public String detail(
            @PathVariable Integer id,
            Model model) {

        // 問題を取得
        Question question = questionMapper.findById(id);

        // 問題が存在しない場合
        if (question == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "指定された問題が見つかりません");
        }

        // 選択肢を取得
        List<Choice> choices =
                choiceMapper.findByQuestionId(id);

        // HTMLへデータを渡す
        model.addAttribute("question", question);
        model.addAttribute("choices", choices);

        return "questions/detail";
    }

    /**
     * 4択問題の解答を採点し、履歴を保存する。
     *
     * POST /questions/{id}/answer
     */
    @PostMapping("/questions/{id}/answer")
    public String answer(
            @PathVariable Integer id,
            @RequestParam Integer selectedChoiceNo,
            Model model) {

        // 問題を取得
        Question question = questionMapper.findById(id);

        if (question == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "指定された問題が見つかりません");
        }

        // 選択肢を取得
        List<Choice> choices =
                choiceMapper.findByQuestionId(id);

        // ユーザーが選択した選択肢を探す
        Choice selectedChoice = null;

        for (Choice choice : choices) {

            if (selectedChoiceNo.equals(choice.getChoiceNo())) {
                selectedChoice = choice;
                break;
            }
        }

        // 存在しない選択肢が指定された場合
        if (selectedChoice == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "選択肢の番号が正しくありません");
        }

        // 正解・不正解を判定
        boolean isCorrect =
                selectedChoiceNo.equals(
                        question.getCorrectChoiceNo());

        // 解答履歴を作成
        AnswerHistory history = new AnswerHistory();

        history.setQuestionId(question.getId());
        history.setChoiceId(selectedChoice.getId());
        history.setMode("FOUR_CHOICE");
        history.setUserAnswer(null);
        history.setIsCorrect(isCorrect);

        // PostgreSQLへ保存
        answerHistoryMapper.insert(history);

        // 採点結果をHTMLへ渡す
        model.addAttribute("question", question);
        model.addAttribute("choices", choices);
        model.addAttribute("selectedChoiceNo", selectedChoiceNo);
        model.addAttribute("isCorrect", isCorrect);

        return "questions/result";
    }

    /**
     * 復習問題一覧画面を表示する。
     *
     * 直近の4択解答が不正解だった問題だけを取得する。
     *
     * GET /questions/review
     */
    @GetMapping("/questions/review")
    public String review(Model model) {

        // 復習対象の問題を取得
        List<Question> questions =
                questionMapper.findQuestionsToReview();

        model.addAttribute("questions", questions);

        return "questions/review";
    }

    /**
     * ○×問題の解答画面を表示する。
     *
     * URLのchoiceNoで出題する選択肢を指定する。
     * 指定がない場合は選択肢1を表示する。
     *
     * GET /questions/{id}/true-false
     */
    @GetMapping("/questions/{id}/true-false")
    public String trueFalse(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "1") Integer choiceNo,
            Model model) {

        // 問題を取得
        Question question = questionMapper.findById(id);

        if (question == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "指定された問題が見つかりません");
        }

        // 選択肢番号が1～4以外ならエラー
        if (choiceNo < 1 || choiceNo > 4) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "選択肢番号は1～4で指定してください");
        }

        // 選択肢を取得
        List<Choice> choices =
                choiceMapper.findByQuestionId(id);

        // 指定された選択肢番号を探す
        Choice selectedChoice = null;

        for (Choice choice : choices) {

            if (choiceNo.equals(choice.getChoiceNo())) {
                selectedChoice = choice;
                break;
            }
        }

        // 指定された選択肢が存在しない場合
        if (selectedChoice == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "指定された選択肢が見つかりません");
        }

        // HTMLへデータを渡す
        model.addAttribute("question", question);
        model.addAttribute("choice", selectedChoice);

        return "questions/true-false";
    }

    /**
     * ○×問題の解答を採点し、履歴を保存する。
     *
     * POST /questions/{id}/true-false/answer
     */
    @PostMapping("/questions/{id}/true-false/answer")
    public String answerTrueFalse(
            @PathVariable Integer id,
            @RequestParam Integer choiceId,
            @RequestParam Boolean userAnswer,
            Model model) {

        // 問題を取得
        Question question = questionMapper.findById(id);

        if (question == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "指定された問題が見つかりません");
        }

        // 選択肢を取得
        List<Choice> choices =
                choiceMapper.findByQuestionId(id);

        // 解答対象の選択肢を探す
        Choice selectedChoice = null;

        for (Choice choice : choices) {

            if (choiceId.equals(choice.getId())) {
                selectedChoice = choice;
                break;
            }
        }

        // この問題に属さない選択肢の場合
        if (selectedChoice == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "選択肢が正しくありません");
        }

        // 選択肢の正解情報を取得
        Boolean correctAnswer =
                selectedChoice.getIsStatementCorrect();

        if (correctAnswer == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "○×問題の正解が登録されていません");
        }

        // 正解・不正解を判定
        boolean isCorrect =
                userAnswer.equals(correctAnswer);

        // 解答履歴を作成
        AnswerHistory history = new AnswerHistory();

        history.setQuestionId(question.getId());
        history.setChoiceId(selectedChoice.getId());
        history.setMode("TRUE_FALSE");
        history.setUserAnswer(userAnswer);
        history.setIsCorrect(isCorrect);

        // PostgreSQLへ保存
        answerHistoryMapper.insert(history);

        // 採点結果をHTMLへ渡す
        model.addAttribute("question", question);
        model.addAttribute("choice", selectedChoice);
        model.addAttribute("userAnswer", userAnswer);
        model.addAttribute("correctAnswer", correctAnswer);
        model.addAttribute("isCorrect", isCorrect);

        return "questions/true-false-result";
    }
}
