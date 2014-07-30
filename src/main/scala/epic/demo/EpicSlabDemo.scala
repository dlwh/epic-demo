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
 * TODO
 *
 * @author dlwh
 **/
object EpicSlabDemo extends SafeLogging {

  val pipeline = {
    MLSentenceSegmenter.bundled("en").get andThen TreebankTokenizer
  }

  def main(args: Array[String]) = {

    val parser = new ParserAnnotator(ParserSelector.loadParser().get)
    val ner = Segmenter.nerSystem(NerSelector.loadNer().get.asInstanceOf[SemiCRF[String, String]]) // ugh, todo
//    val pos = NerSelector.loadNer().get

    for(arg <- args) try {
      val url = if (new File(arg).exists()) new File(arg).toURI.toURL else new URL(arg)

      val preprocessedSlab = pipeline(preprocess.TextExtractor.loadSlab(url))

      logger.info(s"==== parses for $arg ====")
      val parsed = parser(preprocessedSlab)
      for((span, sentence) <- parsed.iterator[Sentence] if span.nonEmpty) {
        val tree = parsed.covered[Tree[AnnotatedLabel]](span).next._2
        println(tree render parsed.covered[Token](span).map(_._2.token).toSeq)
      }

      logger.info(s"=== named entities in $arg ===")
      val nered = ner(preprocessedSlab)
      for((span, sentence) <- nered.iterator[Sentence] if span.nonEmpty) {
        for((espan, entity) <- nered.covered[EntityMention](span)) {
          println((espan.begin, espan.end) + " " + entity.entityType + " " + preprocessedSlab.spanned(espan))
        }
      }


    } catch {
      case ex: Exception =>
        logger.error(s"Error while processing $arg", ex)


    }
  }

}
