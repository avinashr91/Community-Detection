package community.graphml;

/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 */
/*
** GraphMLDemo
** Copyright (C) 2011, Matt Johnson
**
** GraphML.java (Author(s): Matt Johnson)
** 
** Permission is hereby granted, free of charge, to any person obtaining a
** copy of this software and associated documentation files (the "Software"),
** to deal in the Software without restriction, including without limitation
** the rights to use, copy, modify, merge, publish, distribute, sublicense,
** and/or sell copies of the Software, and to permit persons to whom the
** Software is furnished to do so, subject to the following conditions:
**
** The above copyright notice and this permission notice shall be included
** in all copies or substantial portions of the Software. Changes in the
** copyright notice and/or disclaimer are illegal.
**
** THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
** OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
** FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
** ANY MEMBER OF UNCC'S GAME LAB BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
** WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
** CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import community.graph.*;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLReader;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.collections15.BidiMap;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class GraphML {

	Matrix ajMatrix;
	int nodeSize = 0;
	int edgeSize = 0;
	int modEdgeSize = 0;
	int nodeSizeNZD = 0; //record the number of node with non-zero degree
	//The array list, index is the "fake" nodeID used to construct AJMAtrix, element is the true ID
	ArrayList<Integer> nodeIDMapping1 = new ArrayList<Integer>();
	//The array list, index is the true nodeID, element is the "fake" nodeID for AJMatrix construction
	//If the true ID has zero degree, set its element be -1
	ArrayList<Integer> nodeIDMapping2;

	public GraphML(String filename) throws ParserConfigurationException, SAXException, IOException {
		int count = 0;

		// Step 1 we make a new GraphML Reader. We want an Undirected Graph of
		// type node and edge.
		GraphMLReader<UndirectedGraph<Node, Edge>, Node, Edge> gmlr = new GraphMLReader<UndirectedGraph<Node, Edge>, Node, Edge>(
				new VertexFactory(), new EdgeFactory());

		// Next we need a Graph to store the data that we are reading in from
		// GraphML. This is also an Undirected Graph
		// because it needs to match to the type of graph we are reading in.
		final UndirectedGraph<Node, Edge> graph = new UndirectedSparseMultigraph<Node, Edge>();

		gmlr.load(filename, graph);
		// Here we read in our graph. filename is our .graphml file, and graph
		// is where we will store our graph.

		BidiMap<Node, String> vertex_ids = gmlr.getVertexIDs();
		// The vertexIDs are stored in a BidiMap.
		Map<String, GraphMLMetadata<Node>> vertex_meta = gmlr.getVertexMetadata();
		// Our vertex Metadata is stored in a map.
		Map<String, GraphMLMetadata<Edge>> edge_meta = gmlr.getEdgeMetadata();
		// Our edge Metadata is stored in a map.

		// Here we iterate through our vertices, n, and we set the value and the
		// color of our nodes from the data we have
		// in the vertex_ids map and vertex_color map.
		nodeSize = graph.getVertexCount();
		System.out.println("#Nodes: " + nodeSize);
		
	//	Set the length of nodeIDMapping2 be nodeSize, initialize all elements to be -1
		this.nodeIDMapping2 = new ArrayList<Integer>(nodeSize);
		for (int i = 0; i < nodeSize; i++){
			nodeIDMapping2.add(-1);
		}
		
		
		for (Node n : graph.getVertices()) {
			n.setValue(vertex_ids.get(n));
			// Set the value of the node to the vertex_id which was read in from
			// the GraphML Reader.
			// n.setColor(vertex_color.get("d0").transformer.transform(n));
			// Set the color, which we get from the Map, vertex_color.

			// Let's print out the data so we can get a good understanding of
			// what we've got going on.
			// System.out.println("ID: " + n.getID() + ", Value: " +
			// n.getValue() + ", Color: " + n.getColor());
			
			//Find all the node with non-zero degree 
			//Update two ArrayList
			if (graph.degree(n) != 0){
				nodeIDMapping1.add(n.getID());
				nodeIDMapping2.set(n.getID(), nodeIDMapping1.size()-1);
				nodeSizeNZD ++;
			}
			
//			if (count < 10) {
//				System.out.println("ID: " + n.getID() + ", Value: " + n.getValue());
//				count++;
//			}
		}

		System.out.println("#Nodes (Non-zero Degree): " + nodeSizeNZD);
		
		// We initialize an adjacency matrix for the graph information
		ajMatrix = new ValuedSparseMatrix(nodeSizeNZD);

		for (int i = 0; i < nodeSizeNZD; i++) {
			for (int j = 0; j < nodeSizeNZD; j++) {
				ajMatrix.setValue(i, j, 0);
			}
		}

		// Next we store our graph information in a adjacency matrix
		edgeSize = graph.getEdgeCount();
		System.out.println("#Edges: " + edgeSize);
		// Just as we added the vertices to the graph, we add the edges as well.
		for (Edge e : graph.getEdges()) {
		    e.setWeight(Double.parseDouble(edge_meta.get("weight").transformer.transform(e)));
		    double weight = e.getWeight();

			Node node1 = graph.getEndpoints(e).getSecond();
			Node node2 = graph.getEndpoints(e).getFirst();
			
			int node1FakeID = nodeIDMapping2.get(node1.getID());
			int node2FakeID = nodeIDMapping2.get(node2.getID());

				// Set the edge information in the adjacency matrix
				ajMatrix.setValue(node1FakeID, node2FakeID, weight);
				ajMatrix.setValue(node2FakeID, node1FakeID, weight);
				
				modEdgeSize += (int) weight;

			// Set the edge's value.
//			if (count < 20) {
//				//
//				System.out.println("Edge ID :" + e.getID() + " ,Weight: " + e.getWeight() + " , Source : "
//						+ node1.toString() + " , dest : " + node2.toString());
//				count++;
//			}
		}
		
		System.out.println("#Edges (Weighted) : " + modEdgeSize);

//		 //We print the ajMatrix we used to store the graph
//		 System.out.println();
//		 System.out.println("The ajMatrix constructed in GraphML.java is: ");
//		 ajMatrix.printMatrix();

		// // TreeBuilder treeBuilder = new TreeBuilder(graph);
		//
		// // create a simple graph for the demo:
		// // First we make a VisualizationViewer, of type node, edge. We give
		// it
		// // our Layout, and the Layout takes a graph in it's constructor.
		// VisualizationViewer<Node, Edge> vv = new VisualizationViewer<Node,
		// Edge>(new FRLayout<Node, Edge>(graph));
		//
		// // VisualizationViewer<Node, Edge> vv = new VisualizationViewer<Node,
		// // Edge>(
		// // new TreeLayout<Node, Edge>(treeBuilder.getTree()));
		//
		// // Next we set some rendering properties. First we want to color the
		// // vertices, so we provide our own vertexPainter.
		// vv.getRenderContext().setVertexFillPaintTransformer(new
		// vertexPainter());
		// // Then we want to provide labels to our nodes, Jung provides a nice
		// // function which makes the graph use a vertex's ToString function
		// // as it's way of labelling. We do the same for the edge. Look at the
		// // edge and node classes for their ToString function.
		// vv.getRenderContext().setVertexLabelTransformer(new
		// ToStringLabeller<Node>());
		// vv.getRenderContext().setEdgeLabelTransformer(new
		// ToStringLabeller<Edge>());
		//
		// // Next we do some Java stuff, we create a frame to hold the graph
		// final JFrame frame = new JFrame();
		// frame.setTitle("GraphMLReader for Trees - Reading in Attributes");
		// // Set the title of our window.
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// // Give a close operation.
		//
		// // Here we get the contentPane of our frame and add our a
		// // VisualizationViewer, vv.
		// frame.getContentPane().add(vv);
		//
		// // Finally, we pack it to make sure it is pretty, and set the frame
		// // visible. Voila.
		// frame.pack();
		// frame.setVisible(true);
	}

	public Matrix getAjMatrix() {
		return ajMatrix;
	}

	public int getNodeSize() {
		return nodeSizeNZD;
	}

	public int getEdgeSize() {
		return modEdgeSize;
	}
	
	public ArrayList<Integer> getIDMapping1(){
		return this.nodeIDMapping1;
	}
	
	public ArrayList<Integer> getIDMapping2(){
		return this.nodeIDMapping2;
	}
}