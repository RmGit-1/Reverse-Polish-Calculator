# Reverse-Polish-Calculator

# Requirement
動作環境
openjdk 18 2022-03-22

# Howtorun
1. Java ソースコードをコンパイルする
   ```bash
   javac ReversePolishCalculator/Main.java
   ```
2. プログラムを実行する
   ```bash
   java -cp ReversePolishCalculator.Main
   ```

# Usage
## 概要
逆ポーランド記法(Reverse Polish Notation)で入力を行う計算機です。
逆ポーランド記法: https://en.wikipedia.org/wiki/Reverse_Polish_notation

## 基本的な使い方
``` bash
>> [計算式]
```
計算式: 数字,変数名もしくは演算子をスペース区切りで記述
例) "1 + 2 - 3" -> "1 2 + 3 -"

## 使い方の詳細
``` bash
>> help
```

# Lisence
This project is licensed under the MIT License.
