/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.jaliansystems.customiSE.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// a simple JavaFX calculator.
public class Calc extends Application {

    // @formatter:off
    private static final String[][] template = {
        { "7", "8", "9", "/" },
        { "4", "5", "6", "*" },
        { "1", "2", "3", "-" },
        { "0", "c", "=", "+" }
    };
    // @formatter:on
    private final Map<String, Button> accelerators = new HashMap<>();

    private DoubleProperty value = new SimpleDoubleProperty();
    private DoubleProperty operand1 = new SimpleDoubleProperty();
    private DoubleProperty operand2 = null;
    private DoubleProperty currentOperand = operand1;

    private enum Op {
        NOOP, ADD, SUBTRACT, MULTIPLY, DIVIDE, EQUALS
    }

    private Op curOp = Op.NOOP;
    private Op stackOp = Op.NOOP;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) {
        final TextField screen = createScreen();
        final TilePane buttons = createButtons();

        stage.setTitle("Calc");
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setScene(new Scene(createLayout(screen, buttons)));
        Parameters parameters = getParameters();
        Map<String, String> named = parameters.getNamed();
        if (named.containsKey("port")) {
            startListener(Integer.parseInt(named.get("port")));
        }
        stage.show();
    }

    private void startListener(int port) {
        Thread thread = new Thread(new Runnable() {
            @Override public void run() {
                ServerSocket server = null;
                try {
                    server = new ServerSocket(port);
                    while (true) {
                        Socket client = server.accept();
                        new Thread(new Runnable() {
                            @Override public void run() {
                                try {
                                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                    String command;
                                    while ((command = in.readLine()) != null) {
                                        String response = handleCommand(command);
                                        if (response == null)
                                            break;
                                        out.println(response);
                                        out.flush();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            private String handleCommand(String command) {
                                String[] parts = command.split(" ");
                                if (parts.length > 0) {
                                    if (parts[0].equals("click")) {
                                        if (parts.length > 1)
                                            return click(parts[1]);
                                    } else if (parts[0].equals("value")) {
                                        return Integer.toString((int) value.get());
                                    } else if (parts[0].equals("quit")) {
                                        System.exit(0);
                                    }
                                }
                                return "UNKNOWN COMMAND";
                            }

                            private String click(String key) {
                                Button button = accelerators.get(key);
                                if (button == null)
                                    return "ERROR";
                                button.arm();
                                button.fire();
                                try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
                                button.disarm();
                                return "OK";
                            }
                        }).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (server != null)
                            server.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private VBox createLayout(TextField screen, TilePane buttons) {
        final VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: chocolate; -fx-padding: 20; -fx-font-size: 20;");
        layout.getChildren().setAll(screen, buttons);
        handleAccelerators(layout);
        screen.prefWidthProperty().bind(buttons.widthProperty());
        return layout;
    }

    private void handleAccelerators(VBox layout) {
        layout.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent keyEvent) {
                Button activated = accelerators.get(keyEvent.getText());
                if (activated != null) {
                    activated.fire();
                }
            }
        });
    }

    private TextField createScreen() {
        final TextField screen = new TextField();
        screen.setStyle("-fx-background-color: aquamarine;");
        screen.setAlignment(Pos.CENTER_RIGHT);
        screen.setEditable(false);
        screen.setFocusTraversable(false);
        screen.textProperty().bind(Bindings.format("%.0f", value));
        return screen;
    }

    private TilePane createButtons() {
        TilePane buttons = new TilePane();
        buttons.setVgap(7);
        buttons.setHgap(7);
        buttons.setPrefColumns(template[0].length);
        for (String[] r : template) {
            for (String s : r) {
                buttons.getChildren().add(createButton(s));
            }
        }
        return buttons;
    }

    private Button createButton(final String s) {
        Button button = makeStandardButton(s);

        if (s.matches("[0-9]")) {
            makeNumericButton(s, button);
        } else {
            final ObjectProperty<Op> triggerOp = determineOperand(s);
            if (triggerOp.get() != Op.NOOP) {
                makeOperatorButton(button, triggerOp);
            } else if ("c".equals(s)) {
                makeClearButton(button);
            } else if ("=".equals(s)) {
                makeEqualsButton(button);
            }
        }

        return button;
    }

    private ObjectProperty<Op> determineOperand(String s) {
        final ObjectProperty<Op> triggerOp = new SimpleObjectProperty<>(Op.NOOP);
        switch (s) {
        case "+":
            triggerOp.set(Op.ADD);
            break;
        case "-":
            triggerOp.set(Op.SUBTRACT);
            break;
        case "*":
            triggerOp.set(Op.MULTIPLY);
            break;
        case "/":
            triggerOp.set(Op.DIVIDE);
            break;
        }
        return triggerOp;
    }

    private void makeOperatorButton(Button button, final ObjectProperty<Op> triggerOp) {
        button.setStyle("-fx-base: lightgray;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                curOp = triggerOp.get();
                stackOp = triggerOp.get();
            }
        });
    }

    private Button makeStandardButton(String s) {
        Button button = new Button(s);
        button.setStyle("-fx-base: beige;");
        accelerators.put(s, button);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setFocusTraversable(false);
        return button;
    }

    private void makeNumericButton(final String s, Button button) {
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                if (curOp == Op.NOOP) {
                    double newValue = value.get() * 10 + Integer.parseInt(s);
                    value.set(newValue);
                    currentOperand.set(newValue);
                } else {
                    if (curOp == Op.EQUALS) {
                        operand1 = new SimpleDoubleProperty();
                        currentOperand = operand1;
                    } else {
                        operand2 = new SimpleDoubleProperty();
                        currentOperand = operand2;
                    }
                    double newValue = Integer.parseInt(s);
                    value.set(newValue);
                    currentOperand.set(newValue);
                    curOp = Op.NOOP;
                }
            }
        });
    }

    private void makeClearButton(Button button) {
        button.setStyle("-fx-base: mistyrose;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                operand1 = new SimpleDoubleProperty();
                operand2 = null;
                currentOperand = operand1;
                curOp = Op.NOOP;
                stackOp = Op.NOOP;
                value.set(0);
            }
        });
    }

    private void makeEqualsButton(Button button) {
        button.setStyle("-fx-base: ghostwhite;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                if (operand1 == null) {
                    operand1 = new SimpleDoubleProperty(value.doubleValue());
                }
                if (operand2 == null) {
                    operand2 = new SimpleDoubleProperty(value.doubleValue());
                }
                switch (stackOp) {
                case ADD:
                    value.set(operand1.doubleValue() + operand2.doubleValue());
                    break;
                case SUBTRACT:
                    value.set(operand1.doubleValue() - operand2.doubleValue());
                    break;
                case MULTIPLY:
                    value.set(operand1.doubleValue() * operand2.doubleValue());
                    break;
                case DIVIDE:
                    value.set(operand1.doubleValue() / operand2.doubleValue());
                    break;
                case EQUALS:
                case NOOP:
                    break;
                }
                operand1 = null;
                curOp = Op.EQUALS;
            }
        });
    }
}