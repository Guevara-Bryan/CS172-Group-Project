package com.example.springboot;

import java.util.Arrays;

public class Snippet {
  static String generateSnippet(String query, String content) {
    // Returns empty snippet if either query or content is empty
    if(query == "" || content == "")
      return "";

    int snippetLength = 30; // The number of words we want in the snippet

    // Split the content by space into an array
    String[] ogContentTokens = content.split(" ");
    String[] contentTokens = content.replaceAll("[!\"#$%&'()*+,./:;<=>?@\\[\\]^_`{|}~]", "").split(" ");
    
    // No need to generate a snippet if there are less words than the desired snippet length
    if(contentTokens.length <= snippetLength)
      return content;
    
    // Split the query, after words are all lowercase, by space into an array
    String[] queryTokens = query.toLowerCase().split(" ");

    int bestIndex = 0; // The start index of the best generated snippet
    int currentMatches = 0; // The number of matching words in a given temporary window

    // Initialize best snippet
    for(int i = 0; i < snippetLength; i++)
      if(Arrays.stream(queryTokens).anyMatch(contentTokens[i].toLowerCase()::equals))
        currentMatches++;

    int mostMatches = currentMatches; // The number of matching words in the best generated snippet

    // Sliding window algorithm to find the snippet with the most matching words
    for(int i = snippetLength; i < contentTokens.length; i++) {
      // Increment if a match is found
      if(Arrays.stream(queryTokens).anyMatch(contentTokens[i].toLowerCase()::equals))
        currentMatches++;
      
      // Decrement if beginning of the sliding window was a match
      if(Arrays.stream(queryTokens).anyMatch(contentTokens[i - snippetLength].toLowerCase()::equals))
        currentMatches--;
      
      // Update best snippet
      if(currentMatches > mostMatches) {
        mostMatches = currentMatches;
        bestIndex = i - snippetLength;
      }
    }

    // Try to center the matching words in the snippet
    // If there are no matches, the generated snippet starts at the beginning of content
    if(mostMatches == 1) {
      // When there is only one match, the match will always appear at the end of the snippet
      // Center the matching word if possible
      bestIndex += (snippetLength / 2);
      bestIndex = Math.min(bestIndex, contentTokens.length - snippetLength - 1);
    }
    else if(mostMatches > 1) {
      // When there are multiple matches
      int left = bestIndex;
      int right = bestIndex + snippetLength - 1;

      // Find left-most match
      for(;;left++)
        if(Arrays.stream(queryTokens).anyMatch(contentTokens[left].toLowerCase()::equals))
          break;

      // Find right-most match
      for(;;right--)
        if(Arrays.stream(queryTokens).anyMatch(contentTokens[right].toLowerCase()::equals))
          break;
      
      // Try to center the matches
      bestIndex = left - ((snippetLength - (right - left + 1)) / 2);
      bestIndex = Math.max(bestIndex, 0);
      if(bestIndex + snippetLength > contentTokens.length)
        bestIndex = contentTokens.length - snippetLength - 1;
    }

    String snippet = ""; // Stores the best snippet
    // Rejoin the snippet string
    for(int i = bestIndex; i <= bestIndex + snippetLength; i++)
      snippet = snippet + ogContentTokens[i] + " ";

    return snippet;
  }
}
