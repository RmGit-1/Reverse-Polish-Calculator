package ReversePolishCalculator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- 逆ポーランド記法電卓 ---");
        System.out.println("使い方を表示するには\"help\"を入力してください");
        System.out.println();

        Scanner sc = new Scanner(System.in);
        Map<String, Double> variables = new HashMap<>();    // 変数リスト
        Map<String, String> functions = new HashMap<>();    // 演算子リスト

        double ans = 0;                 // 前回の演算結果
        variables.put("Ans", ans); 

        while (true) {
            System.out.print(">> ");
            String in = sc.nextLine();
            
            String[] in_array = in.split(" ");            // スペース区切り
            in_array = removeEmpty(in_array);

            // 入力なし
            if (in_array.length == 0) {
                System.out.println();
                continue;
            }

            // 入力に応じた処理
            A: switch ((in_array[0])) {
                case "help":    // 使い方の表示
                    System.out.println();
                    System.out.println("【コマンド】");
                    System.out.printf("%-27s%s\n", "[exp]", ": 計算式[exp]の結果を表示する");
                    System.out.printf("%-27s%s\n", "define {name} {value} ...", ": 変数{name}を{value}で初期化する(複数入力可)");
                    System.out.printf("%-27s%s\n", "operator {name} {operator} ...", ": 演算{operator}... を行う演算子{name}を定義する");
                    System.out.printf("%-27s%s\n", "assign {name} [exp]", ": 変数{name}に計算式[exp]の結果を代入する");
                    System.out.printf("%-27s%s\n", "vars", ": 変数を一覧表示する (Ansには前回の計算結果が格納される)");
                    System.out.printf("%-27s%s\n", "oprs", ": 演算子を一覧表示する");
                    System.out.printf("%-27s%s\n", "help", ": 使い方を表示する");
                    System.out.printf("%-27s%s\n", "clear", ": 全ての変数・演算子を削除する");
                    System.out.printf("%-27s%s\n", "q", ": プログラムを終了する");
                    System.out.println();
                    System.out.println("【演算子】");
                    System.out.println("a b + : aとbの和");
                    System.out.println("a b - : aとbの差");
                    System.out.println("a b * : aとbの積");
                    System.out.println("a b / : aとbの商");
                    System.out.println("a b % : a÷bの余り");
                    System.out.println("a b ^ : aのb乗");
                    System.out.println();
                    System.out.println("【逆ポーランド記法の計算式】");
                    System.out.println("左から順番に値をスタックに積み、演算子が入力されたら、スタックの上2つの値を演算してスタックに積む");
                    System.out.printf("%-17s%s\n", "例1: 1 + 2 - 3", ">> 1 2 + 3 -");
                    System.out.printf("%-17s%s\n", "例2: 5 * (A + 1)", ">> 5 A 1 + *");
                break;

                case "operator":    // 演算子の定義
                    if (in_array.length < 3) break;
                    String fname = in_array[1];
                    if (fname.matches("^[0-9+\\-*/%^].*")) {  // 数字 or 演算子から始まる文字列
                        System.out.printf("%s: 数字または演算子から始まる演算子名は使用できません\n", fname);
                        break;
                    }
                    else if (variables.containsKey(in_array[1])) {
                        System.out.printf("すでに変数\"%s\"が存在しています\n", in_array[1]);
                        break;
                    }

                    String func = "";
                    // String[] 
                    for (int i=2; i<in_array.length; i++) {
                        if (!in_array[i].matches("[+\\-*/%^]")) {
                            System.out.printf("演算子を定義できません: %sは演算子ではありません\n", in_array[i]);
                            break A;
                        }
                        func += in_array[i] + " ";
                    }
                    functions.put(fname, func);
                    System.out.printf("%s: %s\n", fname, func);
                break;

                case "define":  // 変数の定義
                    for (int i=1; i<in_array.length-1; i+=2) {
                        if (in_array[i].matches("^[0-9+\\-*/%^].*")) {  // 数字 or 演算子から始まる文字列
                            System.out.printf("%s: 数字または演算子から始まる変数名は使用できません\n", in_array[i]);
                        }
                        else if (functions.containsKey(in_array[i])) {
                            System.out.printf("すでに演算子\"%s\"が存在しています\n", in_array[i]);
                        }
                        else{
                            double value = Double.parseDouble(in_array[i+1]);           // 変数に入る値
                            variables.put(in_array[i], value);                          // 変数リストに追加
                            System.out.printf("%s = %f\n", in_array[i], value);  // 格納結果を表示
                        }
                    }
                break;

                case "assign":  // 変数への代入
                    if (in_array[1].matches("^[0-9+\\-*/%^].*")) {  // 数字 or 演算子から始まる文字列
                        System.out.printf("%s: 数字または演算子から始まる変数名は使用できません\n", in_array[1]);
                    }
                    else {
                        double tmp;
                        if (!Double.isNaN(tmp = calculate(in_array, variables, functions, 2))) {   // 計算結果がNaNではない(文法エラーはNaNを返す)
                            variables.put(in_array[1], tmp);                            // 変数に格納
                            System.out.printf("%s = %f\n", in_array[1], tmp);    // 格納結果を表示
                        }
                    }
                break;

                case "vars":    // 変数の一覧表示
                    dispVal(variables);
                break;

                case "oprs":   // 演算子の一覧表示
                    dispFunc(functions);
                break;

                case "clear":   // 変数・演算子の削除
                    variables.clear();
                    functions.clear();
                    variables.put("Ans", 0.0);
                    System.out.println("変数・演算子を削除しました");
                break;

                case "q":   // 終了
                    sc.close();
                    System.exit(0);
                break;
            
                default:    // 計算
                    if (!Double.isNaN(ans = calculate(in_array, variables, functions, 0))) {   // 計算結果がNaNではない(文法エラーはNaNを返す) 
                        variables.put("Ans", ans);  // 変数に格納
                        System.out.println(ans);        // 計算結果を表示
                    }
                break;
            }

            System.out.println();
        }
    }

    // 逆ポーランド記法のString配列から計算結果(実数)を返す
    static double calculate(String[] in_array, Map<String, Double> variables, Map<String, String> functions, int start) {
        double[] stack = new double[in_array.length];   // スタック
            int sp = 0;     // スタックポインタ
            double tmp = 0; // 一時変数

            for (int i = start; i < in_array.length; i++) { 
                // i番目の文字列が
                switch (in_array[i]) {
                    // 演算子の場合
                    case "+", "-", "*", "/", "%", "^": {
                        if (sp<2) return err("演算子の位置が不正です"); // スタックに数字が2つ未満のときエラー
                        
                        tmp = operate(in_array[i], stack, sp);
                        if (Double.isNaN(tmp)) return Double.NaN;
                        
                        // スタックに演算結果を格納してspを1減らす(2つの数 → 1つの数)
                        stack[sp - 1] = 0;
                        stack[sp - 2] = tmp;
                        sp--;
                    } break;

                    // 数字 or 変数の場合
                    default:
                        // 入力が変数リストに含まれていれば
                        if (variables.containsKey(in_array[i])) {
                            stack[sp++] = variables.get(in_array[i]);
                        }

                        // 入力が演算子リストに含まれていれば
                        else if (functions.containsKey(in_array[i])) {
                            for (String op: functions.get(in_array[i]).split(" ")) {
                                if (sp<2) return err("演算子の数または位置が不正です"); // スタックに数字が2つ未満のときエラー
                                tmp = operate(op, stack, sp);
                                if (Double.isNaN(tmp)) return Double.NaN;
                                stack[sp - 1] = 0;
                                stack[sp - 2] = tmp;
                                sp--;
                            }
                        }

                        // 入力が数字ならば
                        else if (in_array[i].matches("-?[\\d]+(.[\\d]+)?")){
                            stack[sp++] = Double.parseDouble(in_array[i]);
                        }
                        
                        // 入力が存在しない変数/演算子名ならばエラー
                        else return err("変数名\"" + in_array[i] + "\"が存在しません");
                    break;
                }
            }

        // 演算子の数が少なすぎる場合エラー
        if (sp!=1) {
            return err("演算子の数が不正です");
        }

        // 演算結果を返す
        return stack[0];
    }
    
    static double operate(String in, double[] stack, int sp) {
        // 演算子ごとの処理
        switch (in) {
            case "+": return stack[sp - 2] + stack[sp - 1];
            case "-": return stack[sp - 2] - stack[sp - 1];
            case "*": return stack[sp - 2] * stack[sp - 1];
            case "/":
                if (stack[sp - 1] == 0) return err("ゼロ除算を行うことはできません");   // 0除算のエラー
                return stack[sp - 2] / stack[sp - 1];
            case "%":
                if (stack[sp - 1] == 0) return err("ゼロ除算を行うことはできません");   // 0除算のエラー
                return stack[sp - 2] % stack[sp - 1];
            case "^":
                return Math.pow(stack[sp - 2], stack[sp - 1]);
        }
        return err("演算エラー");
    }

    // エラー処理用のメソッド、NaNを返す
    static double err(String msg){
        System.out.println("式を計算できませんでした: " + msg);
        return Double.NaN;
    }

    // 変数を昇順ソートして一覧表示する
    static void dispVal(Map<String, Double> map) {
        // 変数リストのコピー
        Map<String, Double> tmpMap = new HashMap<>();
        tmpMap.putAll(map);
        Object[] key = map.keySet().toArray();

        // 昇順ソート
        Arrays.sort(key);

        // 最も長い変数名の文字数を取得
        int maxLen = 1;
        for (Object s: key){
            int len = String.valueOf(s).length();
            if (len > maxLen) maxLen = len;
        }

        // 最長の変数名に合わせてフォーマット、変数を一覧表示
        for (int i=0; i<key.length; i++) {
            System.out.printf("%-" + maxLen + "s = %f\n", String.valueOf(key[i]), tmpMap.get(String.valueOf(key[i])));
        }
    }

    // 演算子を昇順ソートして一覧表示する
    static void dispFunc(Map<String, String> map) {
        // 演算子リストのコピー
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.putAll(map);
        Object[] key = map.keySet().toArray();

        // 昇順ソート
        Arrays.sort(key);

        // 最も長い演算子名の文字数を取得
        int maxLen = 1;
        for (Object s: key){
            int len = String.valueOf(s).length();
            if (len > maxLen) maxLen = len;
        }

        // 最長の演算子名に合わせてフォーマット、演算子を一覧表示
        for (int i=0; i<key.length; i++) {
            System.out.printf("%-" + maxLen + "s: %s\n", String.valueOf(key[i]), tmpMap.get(String.valueOf(key[i])));
        }
    }    

    // 文字列配列から空文字列を削除する
    static String[] removeEmpty(String[] array) {
        int k = 0;
        for (int i=0; i<array.length; i++) {
            if (array[i].equals("")) continue;
            k++;
        }
        String[] array_n = new String[k];
        int j = 0;
        for (int i=0; i<k; j++){
            if (array[j].equals("")) continue;
            array_n[i] = array[j];
            i++;
        }

        return array_n;
    }
}