package com.lexicalanalyzer.nusret.analyzer.Views

import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lexicalanalyzer.nusret.analyzer.R
import com.lexicalanalyzer.nusret.analyzer.Utils.ViewChanger

class TheProject : AppCompatActivity() {

    private var theproject: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_the_project)
        theproject = findViewById(R.id.textView)
        theproject!!.text = Html.fromHtml("<p>LexAn aims to analyze and identify the contextual use of lexical items against a reliable the most frequently used sets of vocabularies, namely General Service List, Academic Word List, Discourse Connectors List, The Academic Collocation List, Frequent Phrasal Verbs and Phrasal Expressions List. Counting the number of words, it calculates lexical density as has been pointed in the relevant literature, one of the major determinants of vocabulary use in productive skills such as writing and speaking is the vocabulary repertoire. With this perspective in mind, our goal is to improve English language learners size and assist them in deploying a skillful use of target lexical items. To the knowledge of the researchers, little attempts have been made to make connections between research findings and the way that technological facilities can be integrated into vocabulary studies.</p>\n" +
                "\n" +
                "<p>Launched in 2018, the major goal of this project is to pull the rich vocabulary resources together by means of LexAn through which it is expected to help language users to develop and use target words more accurately and effectively. This software enables learners to analyze and measure the quality of lexis in writing that can be loaded into the software. The vocabulary richness of a text can be measured by the lexical frequency profile and other add-on features. Furthermore, it is projected that LexAn can enable learners to evaluate the level of their vocabulary use, and raise their awareness in vocabulary use, which might lead to autonomous discovery learning.</p>\n" +
                "\n" +
                "<p>In the following each set will be explained in detail.</p>\n" +
                "\n" +
                " <p><strong> 1. NEW GENERAL SERVICE LIST 1.01 [NGSL] <em>(RENAMED AS NEW-GSL IN THIS PROJECT)</em></strong></p>\n" +
                "\n" +
                "<p>Michael West published a notable list of several thousand important vocabulary words known as the General Service List (GSL) in 1953. As a result of more than two decades of pre-computer corpus research, input from other famous early 20<sup>th</sup> century researchers such as Harold Palmer, and several vocabulary conferences sponsored by the Carnegie Foundation in the 30s, the GSL extended beyond a list of high frequency words, its foremost goal was to combine both objective and subjective criteria to compile a list of words that would be of “general service” to learners of English as a foreign language. However, as useful and helpful as this list has been to us over the decades, it has received criticism on the grounds that (1) it relies on a corpus which is considered to be quite dated, (2) it is too restricted by modern standards (the initial work on the GSL was based on a 2.5 million word corpus that was collected under a grant from the Rockefeller Foundation in 1938), and (3) it falls short of specifying what constitutes a “word”. In March of 2013, on the 60<sub>th</sub> anniversary of West’s publication of the GSL, Charlie Browne at Meiji Gakuin University and his colleagues Dr. Brent Culligan and Joseph Phillips of Aoyama Gakuin Women’s Junior College announced the creation of a New General Service List (NGSL), one that is based on a carefully selected 273 million-word subsection of the 2 billion word Cambridge English Corpus (CEC).</p>\n" +
                "\n" +
                "<p>Following many of the same steps that West and his colleagues did, they have tried to combine the strong objective scientific principles of corpus and vocabulary list creation with useful pedagogic insights to create a list of approximately 2800 high frequency words which meet the following goals:</p>\n" +
                "\n" +
                "<ul>\n" +
                " <li>to update and greatly expand the size of  the corpus used (273 million words compared to the  2.5 million word corpus behind the original GSL), with the hope of increasing the generalizability and validity of the list</li>\n" +
                " <li>to create a list of the most important high-frequency words useful for second language learners of English, ones which gives the highest possible coverage of English texts with the fewest words possible.</li>\n" +
                "</ul>\n" +
                "\n" +
                "<ul>\n" +
                " <li>to make a NGSL that is based on a clearer definition of what constitutes a word</li>\n" +
                " <li>to be a starting point for discussion among interested scholars and teachers around the world, with the goal of updating and revising the list based on this input (in much the same way that West did with the original GSL)</li>\n" +
                "</ul>\n" +
                "\n" +
                "<p>When compared to the GSL, the new list has more comprehensive word families (N=2368) and lemmas (N=2818). This list asserts that the coverage accounts for 87% of a text.</p>\n" +
                "\n" +
                "<p><strong>Reference:</strong> Browne, C., Culligan, B. & Phillips, J. (2013). The New General Service List. Retrieved from    http://www.newgeneralservicelist.org.</p>\n" +
                "\n" +
                "<p><strong>Copyright:</strong> This list is released under a Creative Commons Attribution-ShareAlike 4.0 International License. http://creativecommons.org/licenses/by-sa/4.0</p>\n" +
                "\n" +
                "<p> </p>\n" +
                "\n" +
                " <p><strong>2. NEW GENERAL SERVICE LIST [NEW-GSL] <em>(RENAMED AS NGSL IN THIS PROJECT)</em></strong></p>\n" +

                "\n" +
                "<p>This list results from robust comparison of four language corpora (LOB, BNC, BE06, and EnTenTen12) of the total size of over 12 billion running words. The four corpora were selected to represent a variety of corpus sizes and approaches to representativeness and sampling. It was found out that there exists a stable vocabulary core of 2,122 items (70.7%) among the four corpora. In producing the new-GSL, the core vocabulary items were combined with new items frequently occurring in the corpora representing current language use (BE06 and EnTenTen12). The final product of the study, the new-GSL, consists of 2,494 lemmas and covers between 80.1 and 81.7 per cent of the text in the source corpora.</p>\n" +
                "\n" +
                "<p><strong>Reference:</strong> Brezina, V., & Gablasova, D. (2013). Is there a core general vocabulary? Introducing the New General Service List. <em>Applied Linguistics, 36</em>(1), 1-22.</p>\n" +
                "\n" +
                "<p><strong>Copyright:</strong> This list is released under a Creative Commons Attribution License. http://creativecommons.org/licenses/by/3.0</p>\n" +
                "\n" +
                "<p> </p>\n" +
                "\n" +

                " <p><strong>3. A NEW ACADEMIC VOCABULARY LIST [AVL]</strong></p>\n" +

                "\n" +
                "<p>This list relies on a 120-million-word academic subcorpus of the 425-million-word Corpus of Contemporary American English (COCA; Davies 2012). AVL distinguishes between academic and other types of materials and it accounts for 14 per cent of academic materials in both COCA and the BNC. Academic Vocabulary List improves considerably on the traditional Academic Word List (Coxhead, 2000) in a number of ways. First, while the traditional AWL is based on just 3.5 million words from the 1990s, AVL is based on the 120 million words (in 13,000 academic texts) in the 425-million-word Corpus of Contemporary American English, with texts as recent as 2011. Also, this list is comprised of vast amount word families that did not exist in AWL. It developed by taking the following elements into consideration; frequency, parts of speech, lemma, and format (general vs. technical vocabulary). The number  of AVL is 1992 words and AVL word family size is 7728 words.</p>\n" +
                "\n" +
                "<p><strong>Reference:</strong> Gardner, D., & Davies, M. (2013). A new academic vocabulary list. <em>Applied Linguistics, 35(</em>3), 305-327.</p>\n" +
                "\n" +
                "<p><strong>Copyright:</strong> This list is freely available at https://www.academicvocabulary.info</p>\n" +
                "\n" +
                "<p> </p>\n" +
                "\n" +

                " <p><strong>4. ACADEMIC WORD LIST [AWL] </strong></p>\n" +

                "\n" +
                "<p>The Academic Word List (AWL) was developed by Averil Coxhead. The list contains 570 word families which were selected in compliance with Range, Frequency and Uniformity of frequency.  It is constituted by 10 sublists, each of which includes derivations. The word list has been divided into sublists based on the frequency of occurrence of the words in the Academic Corpus. The words in Sublist 1 occur more frequently in the corpus than the other words in the list. Sublist 2 occurs with the next highest frequency. There are 60 families in each sublist, except for sublist 10 which has 30. The list does not include words that are in the most frequent 2000 words of English and the AWL replaces the University Word List. They are the most frequently occurring vocabulary in academic texts. In English a core academic vocabulary of some 570 words (e.g. words such as evidence, estimate, feature, impact, method, release,) is common to a wide range of academic fields and accounts for around 10% of the words in any academic text.  The AWL was primarily made so that it could be used by teachers as part of a programme preparing learners for tertiary level study or used by students working alone to learn the words most needed to study at tertiary institutions. Students need to be familiar with this vocabulary if they are to complete academic courses successfully.</p>\n" +
                "\n" +
                "<p><strong>Reference:</strong> Coxhead, A. (2000). A new academic word list. <em>TESOL quarterly, 34</em>(2), 213-238.</p>\n" +
                "\n" +
                "<p><strong>Copyright:</strong> This list is freely available at https://www.victoria.ac.nz/lals/resources/academicwordlist/sublists</p>\n" +
                "\n" +
                "<p> </p>\n" +
                "\n" +

                " <p><strong>5. DISCOURSE CONNECTOR LIST [DCL]</strong></p>\n" +
                "\n" +
                "<p>The list includes a taxonomy of 632 discourse connectors in eight broad classes with 17 categories. The frequency of use of each discourse connector listed was analyzed in the three different registers of spoken, non-academic and academic English in the two different cultural contexts of British and American English. The resulting data on discourse connector frequency were compiled in a database and processed with various statistical formulae to highlight multi-register and cross-cultural differences and similarities of use of each discourse connector. An interpretation of the use of this database, which is free to download and use, is included in the study as well as a discussion of the results and the potential for use as a research and pedagogical tool.</p>\n" +
                "\n" +
                "<p><strong>Reference: </strong>Rezvani Kalajahi, S. A. R., Neufeld, S., & Abdullah, A. N. (2017). The discourse connector list: a multi-genre cross-cultural corpus analysis. <em>Text & Talk, 37</em>(3), 283-310.</p>\n" +
                "\n" +
                "<p><strong>Copyright:</strong> This list is available to download as an Excel workbook under a Creative Commons BY-NC-SA license at http://tinyurl.com/thedcl</p>\n" +
                "\n" +
                "<p> </p>\n" +
                "\n" +

                " <p><strong>6. THE ACADEMIC COLLOCATION LIST [ACL]</strong></p>\n" +

                "\n" +
                "<p>The Academic Collocation List (ACL) comprises 2,469 most frequent and pedagogically relevant lexical collocations in written academic English. It was compiled from the written curricular component of the Pearson International Corpus of Academic English (PICAE) comprising over 25 million words. The development involved four stages:</p>\n" +
                "\n" +
                "<ul>\n" +
                " <li>computational analysis of the corpus</li>\n" +
                " <li>refinement of the data-driven list based on quantitative and qualitative parameters</li>\n" +
                " <li>expert review</li>\n" +
                " <li>systematization</li>\n" +
                "</ul>\n" +
                "\n" +
                "<p>While using statistical information to help identify and prioritize the corpus-derived collocational items, Kirsten Ackermann and Yu-Hua Chen (2013) also employed expert judgement to ensure pedagogical relevance as well as usability. By highlighting the most important cross-disciplinary collocations, the ACL can help learners increase their collocational competence and thus their proficiency in academic English. The ACL can also support EAP teachers in their lesson planning and provide a research tool for investigating academic language development.</p>\n" +
                "\n" +
                "<p><strong>Reference:</strong> Ackermann, K., & Chen, Y. H. (2013). Developing the Academic Collocation List (ACL)–A corpus-driven and expert-judged approach. <em>Journal of English for Academic Purposes, 12</em>(4), 235-247.</p>\n" +
                "\n" +
                "<p><strong>Copyright:</strong> This list is freely available at https://ac.els-cdn.com/S1475158513000489/1-s2.0-S1475158513000489-main.pdf?_tid=7bcb1f0d-97a5-4ed2-982b-c630d7e239cb&acdnat=1528286172_24e11c7f1851ad3e8d1cb9822fd46599</p>\n" +
                "\n" +
                "<p> </p>\n" +
                "\n" +

                " <p><strong>7. FREQUENT PHRASAL VERBS [FPV]</strong></p>\n" +

                "\n" +
                "<p>Gardner and Davies (2007) pointed out that many English language teachers have noted the importance of multiword knowledge in developing fluency. Phrasal verbs (e.g., chew out) are crucial to English, and they add a definite richness to the language. However, they also highlighted that there was a general confusion regarding which multiword items to teach and the best ways to include them in language training. Therefore, they analyzed and identified the highest frequent phrasal verb constructions in the 100-million-word British National Corpus. They found that 100 phrasal verbs accounted for more than half of all such items.</p>\n" +
                "\n" +
                "<p><strong>Reference:</strong> Gardner, D., & Davies, M. (2007). Pointing Out Frequent Phrasal Verbs: A Corpus?Based Analysis. <em>TESOL Quarterly, 41</em>(2), 339-359.</p>\n" +
                "\n" +
                "<p><strong>Copyright:</strong> This list is freely available at this https://www.jstor.org/stable/pdf/40264356.pdf?refreqid=excelsior:ba17d3be0e011b46812a7e07f162e698</p>\n" +
                "\n" +
                "<p> </p>\t")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> ViewChanger.changeView(this, 0)
            R.id.TheProject -> ViewChanger.changeView(this, 1)
            R.id.AboutUs -> ViewChanger.changeView(this, 2)
            R.id.ContactUs -> ViewChanger.changeView(this, 3)
        }
        return true
    }
}
