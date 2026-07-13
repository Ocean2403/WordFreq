package com.example.WordFreq;

import com.example.WordFreq.dto.WordFreqDto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootApplication
public class WordFreqApplication {
    private static final List<String> CHAR_SPLIT_PRAPHASE = List.of(".");
    private static final List<String> CHAR_SPLIT_WORD = List.of(" ");
    private static final List<String> CHAR_REMOVE = List.of(
            "~", "!", "@", "#", "$", "%", "^", "&", "*",
            "(", ")", "_", "+", "`", "-", "=", "[", "]",
            ";", "'", ",", "/", "{", "}", ":", "\"",
            "<", ">", "?", "\n"
    );

    public static void main(String[] args) {
        SpringApplication.run(WordFreqApplication.class, args);

        String str = "When Java 1.0 was released in 1995, its API had about a hundred classes, among them java.lang.Thread. Java was the first mainstream programming language that directly supported concurrent programming.\n" +
                "\n" +
                "Since Java 1.2, each Java thread runs on a platform thread supplied by the underlying operating system. (Up to Java 1.1, on some platforms, all Java threads were executed by a single platform thread.)\n" +
                "\n" +
                "Platform threads have nontrivial costs. They require a few thousand CPU instructions to start, and they consume a few megabytes of memory. Server applications can serve so many concurrent requests that it becomes infeasible to have each of them execute on a separate platform thread. In a typical server application, these requests spend much of their time blocking, waiting for a result from a database or another service.\n" +
                "\n" +
                "The classic remedy for increasing throughput is a non-blocking API. Instead of waiting for a result, the programmer indicates which method should be called when the result has become available, and perhaps another method that is called in case of failure. This gets unpleasant quickly, as the callbacks nest ever more deeply.\n" +
                "\n" +
                "JEP 425 introduced virtual threads in Java 19. Many virtual threads run on a platform thread. Whenever a virtual thread blocks, it is unmounted, and the platform thread runs another virtual thread. (The name “virtual thread” is supposed to be reminiscent of virtual memory that is mapped to actual RAM.) Virtual threads became a preview feature in Java 20 (JEP 436) and are final in Java 21.\n" +
                "\n" +
                "With virtual threads, blocking is cheap. When a result is not immediately available, you simply block in a virtual thread. You use familiar programming structures—branches, loops, try blocks—instead of a pipeline of callbacks.\n" +
                "\n" +
                "Virtual threads are useful when the number of concurrent tasks is large, and the tasks mostly block on network I/O. They offer no benefit for CPU-intensive tasks. For such tasks, consider parallel streams or recursive fork-join tasks.";

        handlerWordFreq(str);
    }

    static String removeCharInWords(String str, List<String> charRemoves) {
        if (str == null) {
            return null;
        }

        if (charRemoves == null || charRemoves.isEmpty()) {
            return str;
        }

        for (String charRemove : charRemoves) {
            if (charRemove != null && !charRemove.isEmpty()) {
                str = str.replace(charRemove, "");
            }
        }

        return str;
    }

    static List<String> splitWords(String str, List<String> charSplit) {
        if (str == null) {
            return List.of();
        }

        if (charSplit.isEmpty()) {
            return List.of(str);
        }

        String delimiterPattern = charSplit.stream()
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));

        return Arrays.stream(str.split(delimiterPattern))
                .filter(word -> !word.isBlank())
                .toList();
    }

	static List<WordFreqDto> handlerWordFreq(String str) {
		str = removeCharInWords(str, CHAR_REMOVE);

		List<String> paragraphs = splitWords(str, CHAR_SPLIT_PRAPHASE);

		Map<String, Long> frequencyMap = paragraphs.stream()
				.flatMap(paragraph ->
						splitWords(paragraph, CHAR_SPLIT_WORD).stream()
				)
				.map(String::trim)
				.filter(word -> !word.isBlank())
				.collect(Collectors.groupingBy(
						word -> word,
						LinkedHashMap::new,
						Collectors.counting()
				));

		return frequencyMap.entrySet()
				.stream()
				.map(entry -> WordFreqDto.builder()
						.word(entry.getKey())
						.freq(entry.getValue().intValue())
						.build())
				.toList();
	}
}
