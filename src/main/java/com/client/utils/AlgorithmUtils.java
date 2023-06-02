package com.client.utils;

import java.util.List;
import java.util.Objects;

/**
 * 算法工具类
 *
 * @author 02
 */
public class AlgorithmUtils {

    /**
     * 编辑距离算法（用于计算最相似的两组标签）
     * 原理：https://blog.csdn.net/DBC_121/article/details/104198838
     *
     *
     * 首先，获取两个字符串 word1 和 word2 的长度，分别保存在变量 n 和 m 中。
     *
     * 如果其中一个字符串为空（长度为0），则返回另一个字符串的长度，因为此时最小编辑距离就是将另一个字符串插入到空字符串中的操作次数。
     *
     * 创建一个二维数组 d，用于保存编辑距离。数组的行数为 n+1，列数为 m+1。这里的 d[i][j] 表示将 word1 的前 i 个字符转换为 word2 的前 j 个字符所需的最小操作次数。
     *
     * 初始化数组 d 的第一行和第一列。d[i][0] 表示将 word1 的前 i 个字符转换为空字符串所需的操作次数，即删除操作；d[0][j] 表示将空字符串转换为 word2 的前 j 个字符所需的操作次数，即插入操作。
     *
     * 使用动态规划的思想，计算编辑距离。遍历 word1 的每个字符，从第二行和第二列开始。对于每个位置 (i, j)，根据其左边、上边和左上角的三个位置的编辑距离，计算出当前位置的编辑距离。
     *
     * 在计算当前位置 (i, j) 的编辑距离时，有三种操作可选：
     *
     * 删除操作：将 word1 的第 i 个字符删除，编辑距离加1，即 left = d[i - 1][j] + 1。
     * 插入操作：将 word2 的第 j 个字符插入到 word1 的第 i 个字符后面，编辑距离加1，即 down = d[i][j - 1] + 1。
     * 替换操作：将 word1 的第 i 个字符替换为 word2 的第 j 个字符，如果两个字符不相同，编辑距离加1，即 left_down = d[i - 1][j - 1] + (word1.charAt(i - 1) != word2.charAt(j - 1) ? 1 : 0)。
     * 选择这三种操作中的最小编辑距离作为当前位置 (i, j) 的编辑距离，即 d[i][j] = Math.min(left, Math.min(down, left_down))。
     *
     * 遍历完成后，d[n][m] 即为将 word1 转换为 word2 所需的最小编辑距离，将其作为函数的返回值。
     * @param tagList1 目标字符串
     * @param tagList2 转换字符串
     * @return
     */
    //动态规划,gnn实现相似字符串数组进行比较 获得对应字符串数组变成目标数组需要改变的次数
    public static int minDistance(List<String> tagList1, List<String> tagList2) {
        int n = tagList1.size();
        int m = tagList2.size();

        if (n * m == 0) {
            return n + m;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1; //删除
                int down = d[i][j - 1] + 1; //插入
                int left_down = d[i - 1][j - 1];
                if (!Objects.equals(tagList1.get(i - 1), tagList2.get(j - 1))) {
                    left_down += 1;//替换
                }
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }

    /**
     * 编辑距离算法（用于计算最相似的两个字符串）
     * 原理：https://blog.csdn.net/DBC_121/article/details/104198838
     *
     * @param word1
     * @param word2
     * @return
     */
    public static int minDistance(String word1, String word2) {
        int n = word1.length();
        int m = word2.length();

        if (n * m == 0) {
            return n + m;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (word1.charAt(i - 1) != word2.charAt(j - 1)) {
                    left_down += 1;
                }
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }
}
