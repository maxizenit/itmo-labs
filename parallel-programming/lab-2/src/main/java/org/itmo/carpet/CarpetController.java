package org.itmo.carpet;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.itmo.carpet.model.Carpet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;

public class CarpetController {

    @FXML
    private Spinner<Integer> iterationsSpinner;
    @FXML
    private Spinner<Integer> threadsCountSpinner;
    @FXML
    private ColorPicker backgroundColorPicker;
    @FXML
    private ColorPicker carpetColorPicker;
    @FXML
    private Button generateButton;
    @FXML
    private Label resultLabel;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Canvas canvas;
    @FXML
    private Slider scaleSlider;

    //необходим для выполнения задач вне главного (графического) потока
    private final ExecutorService asyncExecutor = Executors.newSingleThreadExecutor();

    private Carpet currentCarpet;

    public void initialize() {
        initializeIterationsSpinner();
        initializeThreadsCountSpinner();
        initializeScrollPane();
    }

    /**
     * Инициализирует поле выбора количества итераций (не должно быть меньше 1).
     */
    private void initializeIterationsSpinner() {
        SpinnerValueFactory<Integer> integerSpinnerValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1);
        iterationsSpinner.setValueFactory(integerSpinnerValueFactory);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[1-9][0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        iterationsSpinner.getEditor().setTextFormatter(formatter);
    }

    /**
     * Инициализирует поле выбора количества потоков (не должно быть меньше 1 и больше количества ядер процессора).
     */
    private void initializeThreadsCountSpinner() {
        SpinnerValueFactory<Integer> threadsCountSpinnerValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
                        Runtime.getRuntime().availableProcessors(), 1);
        threadsCountSpinner.setValueFactory(threadsCountSpinnerValueFactory);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[1-9][0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        threadsCountSpinner.getEditor().setTextFormatter(formatter);
    }

    /**
     * Инициализирует панель, в которую вложен канвас (панель скроллится по обоим осям координат).
     */
    private void initializeScrollPane() {
        scrollPane.setPannable(true);//возможность перетаскивания контента
        scrollPane.setFitToHeight(true);//подстройка высоты под высоту контента внутри
        scrollPane.setFitToWidth(true);//подстройка длины под длину контента внутри
    }

    @FXML
    private void onGenerateButtonClick() {
        generateButton.setDisable(true);
        scaleSlider.setDisable(true);
        resultLabel.setText("");
        clearCanvas();
        asyncExecutor.submit(this::generateAndDrawCarpet);
    }

    @FXML
    private void onScaleSliderMouseReleased() {
        drawCarpet();//при изменении масштаба фрактал перерисовывается
    }

    @FXML
    private void onBackgroundColorPickerAction() {
        drawCarpet();//при изменении цвета фона фрактал перерисовывается
    }

    @FXML
    private void onCarpetColorPickerAction() {
        drawCarpet();//при изменении цвета фрактала фрактал перерисовывается
    }

    /**
     * Очищает канвас.
     */
    private void clearCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Генерирует и рисует канвас.
     */
    private void generateAndDrawCarpet() {
        long startTime = System.currentTimeMillis();
        currentCarpet = CarpetGenerator.generate(iterationsSpinner.getValue(), threadsCountSpinner.getValue());
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;//время выполнения генерации

        Platform.runLater(() -> {
            //если большой масштаб может привести к ошибкам, он устанавливается в 1
            if (needScaleSliderDisable()) {
                scaleSlider.setValue(1);
            }
            drawCarpet();
            generateButton.setDisable(false);
            if (!needScaleSliderDisable()) {
                scaleSlider.setDisable(false);
            }
            resultLabel.setText(String.format("Результат: %d мс", elapsedTime));
        });
    }

    /**
     * Рисует фрактал.
     */
    private void drawCarpet() {
        Carpet carpet = currentCarpet;
        if (currentCarpet == null) {
            return;
        }
        Color carpetColor = carpetColorPicker.getValue();
        Color backgroundColor = backgroundColorPicker.getValue();
        double scale = scaleSlider.getValue();

        canvas.setWidth(carpet.size() * scale);
        canvas.setHeight(carpet.size() * scale);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(carpetColor);
        gc.fillRect(0, 0, canvas.getWidth() * scale, canvas.getHeight() * scale);

        gc.setFill(backgroundColor);
        carpet.internalSquares().forEach(square ->
                gc.fillRect(square.x() * scale,
                        square.y() * scale,
                        square.size() * scale,
                        square.size() * scale));
    }

    /**
     * Возвращает {@code true}, если необходимо запретить перетаскивать ползунок изменения масштаба.
     * На особо больших размерах фрактала его масштабирование может вызвать ошибки.
     *
     * @return {@code true}, если необходимо запретить перетаскивать ползунок изменения масштаба
     */
    private boolean needScaleSliderDisable() {
        return currentCarpet.size() > Math.pow(3, 6);
    }
}