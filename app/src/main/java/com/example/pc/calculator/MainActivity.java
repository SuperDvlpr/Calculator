package com.example.pc.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.mariuszgromada.math.mxparser.Expression;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {


    TextView textView, textView2;
    HorizontalScrollView horizontalScrollView;
    ScrollView scrollView;

    boolean buttonAClicked = false;
    boolean result = false;
    boolean preNum = false;
    boolean negativeNum = false;
    boolean power = false;
    String answer;
    String positiveString;
    int funcCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        scrollView = findViewById(R.id.scrollView2);

        Button OnClickDelete = findViewById(R.id.button20);
        OnClickDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textView.getText().toString().contains("E") || textView.getText().toString().contains(getString(R.string.error))) {
                    deleteResult();
                } else  if (textView.getText().length() != 0){
                    deleteSymbol(textView);
                    if (result) {
                        result = false;
                        textView2.setText("");
                    }
                } else if (textView2.getText().length() > 0){
                    deleteFromTopView();
                }
            }
        });

        //Удаление всех символов
        OnClickDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteResult();
                return false;
            }
        });



    }

    //Кнопки 0..9
    public void onClickButtonNumber(View view) {
        if (result) {
            deleteResult();
        }
        if (preNum) {
            textView.setText("");
            preNum = false;
        }
        if (textView.getText().length() < 13) {
            if (textView.getText().equals("0") || textView.getText().equals("-0")) {
                textView.setText(((Button)view).getText());
            } else {
                textView.setText(textView.getText().toString() + ((Button)view).getText());
            }
            buttonAClicked = false;
        }
        scroller(horizontalScrollView);
        power = false;
    }

    //Кнопки +, -, *, /
    public void onClickButtonAction(View view) {
        clearValue();
        if (textView2.getText().length() != 0 || textView.getText().length() != 0) {
            String string = (String) ((Button)view).getText();
            if (textView.getText().toString().contains(getString(R.string.error))) {
                deleteResult();
            } else if (textView2.getText().length() != 0) {
                if (power && textView.getText().length() == 0) {
                    deleteSymbol(textView2);
                    deleteSymbol(textView2);
                    power = false;
                }
                if (buttonAClicked && textView2.getText().length() >= 1 && (checkLast("+") || checkLast("-") || checkLast("*") || checkLast("/"))) {
                    deleteSymbol(textView2);
                    textView2.setText(textView2.getText() + "" + string);
                    textView.setText("");
                    preNum = true;
                } else if (result) {
                    textView2.setText(textView2.getText() + "" + string);
                    result = false;
                    preNum = true;
                    funcCounter = 0;
                } else {
                    for (int i = 0; i <10; i++) {
                        if (textView2.getText().toString().substring(textView2.getText().length() - 1, textView2.getText().length()).equals(i + "")) {
                            textView2.setText(textView2.getText() + string);
                            return;
                        }
                    }
                    if (textView.getText().length() != 0) {
                        textView2.setText(textView2.getText() + "" + textView.getText() + string);
                    } else {
                        textView2.setText(textView2.getText() + string);
                    }
                    preNum = true;
                }
            } else {
                textView2.setText(textView.getText() + "" + string);
                preNum = true;
            }
        }
    }

    //Равно
    public void onClickEqually(View view) {
        clearValue();
        //Проверка на закрытость скобок
        if (!result) textView2.setText(textView2.getText() + "" + textView.getText());
        String resultString = textView2.getText().toString();
        int countBtkOpen = 0, countBtkClose = 0, difference;
        for (char element : resultString.toCharArray()){
            if (element == '(') countBtkOpen++;
            if (element == ')') countBtkClose++;
        }
        //Изменение строки, если количество открытых скобок не равно количеству закрытых
        difference = countBtkOpen-countBtkClose;
        if (difference > 0) {
            for (int i = 0; i < difference; i++) {
                textView2.setText(textView2.getText() + ")");
            }
        }
        if (difference < 0) {
            for (int i = 0; i > difference; i--) {
                textView2.setText("(" + textView2.getText());
            }
        }
        if (textView2.getText().length() != 0) {

            String a = correctString(textView2.getText().toString());
            textView2.setText(correctString(textView2.getText().toString()));
            Expression x = new Expression(a);
            try {
                answer = String.valueOf(new BigDecimal("" + x.calculate()).setScale(10, BigDecimal.ROUND_HALF_UP));
                textView.setText("");
                textView.setText(answer);
                result = true;
            } catch (Exception e) {
                textView.setText(getString(R.string.error));
            }
            result = true;
            clearValue();
            if (textView.getText().equals("0E-10")) {
                textView.setText("0");
            }
            buttonAClicked = false;
            power = false;
        }
    }

    // Точка
    public   void onClickDot(View view) {
        if (textView.getText().toString().contains(getString(R.string.error))) {
            deleteResult();
        }
        if (!textView.getText().toString().contains(".") && textView.getText().length() < 12 && !textView.getText().equals("-")) {
            if (textView.getText().length() == 0) {
                textView.setText("0.");
            } else {
                textView.setText(textView.getText() + ".");
                if (result) {
                    result = false;
                    textView2.setText("");
                    funcCounter = 0;
                }
                scroller(horizontalScrollView);
            }
        }
    }

    //Кнопка ±
        public void onClickPlusMinus(View view) {
            if (textView.getText().toString().contains(getString(R.string.error))) {
                deleteResult();
            }
            positiveString = textView.getText().toString().replace("-" , "");
            if (!negativeNum && !textView.getText().toString().contains("-")) {
                textView.setText("-" + positiveString);
                negativeNum = true;
            } else {
                textView.setText(positiveString);
                negativeNum = false;
            }
            if (result) {
                result = false;
                textView2.setText("");
                funcCounter = 0;
            }

        }

    //Скобка (
    public void onClickOpenBracket (View view) {
        if (!result) {
            if (result) {
                textView2.setText("");
                funcCounter = 0;
            }

            textView2.setText(textView2.getText() + "(");
            buttonAClicked = false;
        }
    }

    //Скобка )
    public void  onClickCloseBracket (View view) {
        if (!result) {
            textView2.setText(textView2.getText() + "" + textView.getText() + ")");
            textView.setText("");
            buttonAClicked = false;
            power = false;
        }
    }

    //Пи
    public void onClickPi (View view) {
        double Pi = 3.1415926535;
        textView.setText(String.valueOf(Pi));
    }

    //Число е
    public void onClickE (View view) {
        double e =2.7182818284;
        textView.setText(String.valueOf(e));

    }

    //е в степени
    public void onClickEStepen (View view) {
        double e =2.7182818284;
        textView2.setText(String.valueOf(e) + "^");
    }

    //Функциональные кнопки калькулятора
    public void onClickFun (View view) {
        textView2.setText(textView2.getText() + "" + (((Button)view).getText()) + "(" + textView.getText());
        textView.setText("");
        funcCounter++;
    }

    //1/x
    public void onClickOneDelit (View view) {
        textView2.setText(textView2.getText() + "" + "1/" + "(" + textView.getText());
        textView.setText("");
        funcCounter++;
    }

    //Корень
    public void onClickCoren (View view) {
        textView2.setText(textView2.getText() + "sqrt(" + textView.getText());
    }

    //Степень
    public void onClickStepen (View view) {
        clearValue();
        if (textView2.getText().length() != 0 || textView.getText().length() != 0) {
            if (buttonAClicked) {
                deleteSymbol(textView2);
                textView2.setText(textView2.getText() + "^(");
                power = true;
                buttonAClicked = false;
            } else if (textView.getText().toString().contains(getString(R.string.error))) {
                deleteResult();
            } else if (result) {
                textView2.setText(textView2.getText() + "^(");
                funcCounter = 0;
                result = false;
                preNum = true;
            } else {
                textView2.setText(textView2.getText() + "" + textView.getText() + "^(");
                preNum = true;
                power = true;
            }
            textView.setText("");
        }
    }

    //Х в квадрате
    public void onClickXSqd (View view) {
        if (textView.getText().length() !=0) {
            textView2.setText(textView.getText() + "^2");
            textView.setText("");
        }
    }

    //Скрол в конец строки
    public void scroller (final HorizontalScrollView horizontalScroll){
        horizontalScroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                horizontalScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100L);
    }

    //Очистка результата
    public void deleteResult () {
        textView.setText("");
        textView2.setText("");
        buttonAClicked = false;
        result = false;
        preNum = false;
        power = false;
        negativeNum = false;
        funcCounter = 0;
    }

    // Очистка нижнего поля от лишних нулей и точек
    public void clearValue (){
        if (textView.getText().equals("-")) textView.setText("");
        if (textView.getText().toString().contains(".")) {
            cleaning:
            while (true) {
                int i = textView.getText().length();
                String s = textView.getText().toString().substring(i - 1, i);
                switch (s) {
                    case "0":
                        deleteSymbol(textView);
                        break;
                    case ".":
                        deleteSymbol(textView);
                        break cleaning;
                    default:
                        break cleaning;
                }
            }
        }
    }

    //Удаление последнего символа в TextView
    public void deleteSymbol(TextView tV) {
        StringBuilder stringBuffer = new StringBuilder(tV.getText().toString());
        if (stringBuffer.length() != 0) {
            stringBuffer.delete(tV.getText().length() - 1, tV.getText().length());
            tV.setText(stringBuffer.toString());

        }
    }

    //Проверка последнего символа
    public boolean checkLast (String symbol) {
        int l = textView2.getText().length();
        if (textView2.getText().length() != 0) {
            return textView2.getText().toString().substring(l - 1, l).equals(symbol);
        } else return false;
    }

    //Удаление из верхнего поля
    public void deleteFromTopView() {
        if (textView2.getText().length() != 0) {
            if (checkLast("(")){
                deleteSymbol(textView2);
                if (checkLast("g") || checkLast("s")) {
                    for (int i = 0; i < 3; i++){
                        deleteSymbol(textView2);
                    }
                } else if (checkLast("n")) {
                    deleteSymbol(textView2);
                    if (checkLast("l")) {
                        deleteSymbol(textView2);
                    } else {
                        deleteSymbol(textView2);
                        deleteSymbol(textView2);
                    }
                } else if (checkLast("t")) {
                    for (int i = 0; i < 4; i++){
                        deleteSymbol(textView2);
                    }
                } else {
                    deleteSymbol(textView2);
                }
            } else {
                deleteSymbol(textView2);
            }
            if (checkLast(".")) {
                deleteSymbol(textView2);
            }

        }
    }

    //Исправление строки
    public String correctString (String string){

        string = string.replace("--", "+");
        string = string.replace("+-", "-");

        string = string.replace("(+", "(");
        string = string.replace("(*", "(");
        string = string.replace("(/", "(");

        string = string.replace("+)", ")");
        string = string.replace("-)", ")");
        string = string.replace("*)", ")");
        string = string.replace("/)", ")");

        string = string.replace("()", "(0)");
        string = string.replace(")(", ")*(");

        String[] func = {"c", "s", "t", "l"};
        for (int i = 0; i <10; i++){
            string = string.replace(i + "(", i + "*(" );
            string = string.replace(")" + i, ")*" + i );
        }
        for (int f = 0; f <4; f++){
            string = string.replace(")" + func[f], ")*" + func[f]);
            for (int i = 0; i <10; i++){
                string = string.replace(i + "" + func[f], i + "*" + func[f]);
            }
        }
        return string;
    }

    //Востановлние активити при повороте
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textView.setText(savedInstanceState.getString("textView"));
        textView2.setText(savedInstanceState.getString("textView2"));
        positiveString = savedInstanceState.getString("positiveString");
        buttonAClicked = savedInstanceState.getBoolean("buttonAClicked");
        result = savedInstanceState.getBoolean("result");
        preNum = savedInstanceState.getBoolean("preNum");
        negativeNum = savedInstanceState.getBoolean("negativeNum");
        power = savedInstanceState.getBoolean("power");
        funcCounter = savedInstanceState.getInt("funcCounter");
    }



    //Сохранение Активити
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("textView", textView.getText().toString());
        outState.putString("textView2", textView2.getText().toString());
        outState.putString("positiveString", positiveString);
        outState.putBoolean("buttonAClicked", buttonAClicked);
        outState.putBoolean("result", result);
        outState.putBoolean("preNum", preNum);
        outState.putBoolean("negativeNum", negativeNum);
        outState.putBoolean("power", power);
        outState.putInt("funcCounter", funcCounter);
    }
}
