package com.nowcoder.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);


    //    实现 InitializingBean 下的 afterPropertiesSet 类，在初始化的时候 读取敏感词文本
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineText;
            while ((lineText = bufferedReader.readLine()) != null) {
                addWord(lineText);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }


    //    增加敏感关键词
    private void addWord(String lineText) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < lineText.length(); ++i) {
            Character c = lineText.charAt(i);

            TrieNode node = tempNode.getSubNode(c);
            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;
            if (i == lineText.length() - 1) {
                node.setKeywordEnd(true);
            }
        }
    }

    private class TrieNode {
        //        是否当前关键词的结尾
        private boolean end = false;

        //        当前节点下所有的子节点
        private Map<Character, TrieNode> subNodes = new HashMap<Character, TrieNode>();

        public void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeyWordEnd() {
            return end;
        }

        void setKeywordEnd(boolean end) {
            this.end = end;
        }
    }

    private TrieNode rootNode = new TrieNode();

//    判断是否是符号
    private boolean isSymbol(char c){
        int ic = (int)c;
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return text;
        }

        StringBuilder result = new StringBuilder();

        String  replacement = "***";
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        while (position < text.length()){
            char c = text.charAt(position);

//            过滤敏感词中间的符号
            if (isSymbol(c)){
                if (tempNode == rootNode){
                    result.append(c);
                    ++begin;
                }

                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                result.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            }else if (tempNode.isKeyWordEnd()){
//                发现敏感词
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            }else {
                ++position;
            }
        }
        logger.info("subString text : " + text.substring(begin));
        result.append(text.substring(begin));//？？
        return result.toString();
    }

    public static void main(String[] args) {
        SensitiveService s = new SensitiveService();
        s.addWord("色情");
        s.addWord("赌博");
        System.out.println(s.filter("白色 情人节. 游戏怎么会无聊。\n 垃圾"));


    }

}
