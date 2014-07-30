package epic.demo

import java.io.File
import java.net.URL

import epic.models.{NerSelector, ParserSelector}
import epic.parser.ParserAnnotator
import epic.preprocess
import epic.preprocess.{TreebankTokenizer, MLSentenceSegmenter}
import epic.sequences.{SemiCRF, Segmenter}
import epic.slab.{EntityMention, Token, Sentence}
import epic.trees.{AnnotatedLabel, Tree}
import epic.util.SafeLogging

/**
 * Demonstrates parsing and NER in epic without using slabs (much)
 *
 * @author dlwh
 **/
object EpicSeqDemo extends SafeLogging {

  def main(args: Array[String]) = {

    val parser = ParserSelector.loadParser().get
    val ner = NerSelector.loadNer().get
    //    val pos = NerSelector.loadNer().get

    for(arg <- args) try {
      val url = if (new File(arg).exists()) new File(arg).toURI.toURL else new URL(arg)

      val preprocessed = preprocess.preprocess(preprocess.TextExtractor.extractText(url))

      logger.info(s"==== parses for $arg ====")
      val parsed = preprocessed.par.map(parser).seq
      for((tree, sentence) <- parsed zip preprocessed) {
        println(tree render sentence)
      }

      logger.info(s"=== named entities in $arg ===")
      val nered = preprocessed.par.map(ner.bestSequence(_)).seq
      for(seg <- nered) {
        println(seg.render)
      }


    } catch {
      case ex: Exception =>
        logger.error(s"Error while processing $arg", ex)


    }
  }

}
