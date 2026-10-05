package saka1029.csp;

import java.util.function.Consumer;

public interface Solver {
    /**
     * 出力先の変更
     * @param s
     */
    void out(Consumer<String> s);
    /**
     * エラー出力先の変更
     * @param s
     */
    void err(Consumer<String> s);
    /**
     * 問題を解き、結果を標準出力および標準エラー出力に出力する。
     * @return 見つかった解の数を返す。
     */
    int solve();
    /**
     * 問題を解き、解が見つかる都度、callbackを呼び出す。
     * @param callback int配列にはその時見つかった解が格納されている。
     *                 int配列に格納される解の順番はvariables()が返す変数名の順序と一致する。
     * @return 見つかった解の数を返す。
     */
    int solve(Consumer<int[]> callback);
    /**
     * @return 変数名の一覧を返す。
     */
    String[] variables();
}
