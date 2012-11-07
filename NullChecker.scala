package toolbox.script
import com.ensoftcorp.atlas.java.core.script.Common
import com.ensoftcorp.atlas.java.core.index.NameFactory
import com.ensoftcorp.atlas.java.core.db.Accuracy

object NullChecker {

  import com.ensoftcorp.atlas.java.core.query.Q
  import com.ensoftcorp.atlas.java.core.query.Attr
  import com.ensoftcorp.atlas.java.core.query.Attr.Edge
  import com.ensoftcorp.atlas.java.core.query.Attr.Node
  import com.ensoftcorp.atlas.java.core.script.Common._
  import com.ensoftcorp.atlas.java.core.highlight._
  import com.ensoftcorp.atlas.java.interpreter.lib.Common._
  import com.ensoftcorp.atlas.java.core.db.graph.Graph
  import com.ensoftcorp.atlas.java.core.db.graph.GraphElement
  import com.ensoftcorp.atlas.ui.viewer.graph.DisplayUtil
  import java.awt.Color

  /**
   * Get the dataflow edges going to the given node
   */
  def flow(x:Q) = {
    edges(Edge.DATA_FLOW).reverse(edges(Edge.DECLARES).forward(x))
  }

  def showHighlightedSubgraph(x:Q) = {
    var t = edges(Edge.DATA_FLOW).reverse(edges(Edge.DECLARES).forward(x))
    var y = check(x)
    
    var h = new Highlighter()
    h.highlightEdges(y, Color.ORANGE)
    h.highlightNodes(y.roots(), Color.RED)
    DisplayUtil.displayGraph(t.eval(), h)
  }
  
  /**
   * Check whether a null value ever flows into the given node
   * 
   * @returns the flow from a null value to the given node (empty if no flow)
   */
  def check(x:Q):Q = {
    var t = edges(Edge.DATA_FLOW).reverse(edges(Edge.DECLARES).forward(x))
    var graph = t.eval()
    
    var nodes = graph.nodes()
    var iter = nodes.iterator()
    while (iter.hasNext()) {
      var result = recursiveCheck(graph, iter.next(), "")
      if (result != null) {
        println(result.attr())
        var sourceQ = toQ(toGraph(result))
        var destQ = t.leaves()
        var toRet = t.between(sourceQ, destQ);
        
        println(toRet.eval().nodes().size(Accuracy.APPROXIMATE))
        
	  	return toRet;
      }
    }
    
  	return empty();
  }
  
  /**
   * Recursively check the given graph and graph element to see if
   * a null value is ever assigned to it.
   * 
   * @return the GraphElement that is assigned to null (null if it 'null' isn't assigned)
   */
  def recursiveCheck(graph:Graph, g:GraphElement, indent:String):GraphElement = {
    var attrs = g.attr()
    var id = attrs.get("id")
    
    // TODO: not sure if this is a good check for being assigned null...
    if (id != null && id.toString().contains("org.eclipse.jdt.core.dom.NullLiteral")) {
      println("NULLABLE assigned to NONNULL")
      return g;
    }

    var edgeIter = graph.edges(g, GraphElement.NodeDirection.IN).iterator()
    while (edgeIter.hasNext()) {
      var tmp = recursiveCheck(graph, edgeIter.next(), indent + "\t")
      if (tmp != null) {
        return tmp;
      }
    }
    
    return null;
  } 
}
