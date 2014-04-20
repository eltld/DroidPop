SELECT [Word].[_word],
  [Paraphrase].[_id],
  [Paraphrase].[_categore],
  [Paraphrase].[_detail],
  [Demo].[_demo]
FROM [Word]
  INNER JOIN [Paraphrase] ON [Word].[_id] = [Paraphrase].[_word_id]
  LEFT JOIN [Demo] ON [Paraphrase].[_id] = [Demo].[_paraphrase_id]  
WHERE
  [Word].[_id] = ?