package scanpath;

import perspectives.base.Property;
import perspectives.properties.PString;
import perspectives.tree.Tree;
import perspectives.tree.TreeNode;
import perspectives.util.DistancedPoints;
import ch.usi.inf.sape.hac.HierarchicalAgglomerativeClusterer;
import ch.usi.inf.sape.hac.agglomeration.AverageLinkage;
import ch.usi.inf.sape.hac.agglomeration.SingleLinkage;
import ch.usi.inf.sape.hac.dendrogram.Dendrogram;
import ch.usi.inf.sape.hac.dendrogram.DendrogramBuilder;
import ch.usi.inf.sape.hac.dendrogram.DendrogramNode;
import ch.usi.inf.sape.hac.dendrogram.ObservationNode;
import ch.usi.inf.sape.hac.experiment.DissimilarityMeasure;
import ch.usi.inf.sape.hac.experiment.Experiment;

public class HACClusteringHelper implements Experiment, DissimilarityMeasure{
	public static final String PROPERTY_ID = "Id";
	float[][] distanceMatrix;
	DistancedPoints distancedPoints;
	public HACClusteringHelper(float[][] distanceMatrix, DistancedPoints distancedPoints)
	{
		this.distanceMatrix = distanceMatrix;
		this.distancedPoints = distancedPoints;
	}
	@Override
	public int getNumberOfObservations() {
		// TODO Auto-generated method stub
		return this.distanceMatrix.length;
	}

	@Override
	public double computeDissimilarity(Experiment experiment, int observation1,
			int observation2) {
		// TODO Auto-generated method stub
		return this.distanceMatrix[observation1][observation2];
	}

	public Tree getTree()
	{
		DendrogramBuilder dendrogramBuilder = new DendrogramBuilder(getNumberOfObservations());
		HierarchicalAgglomerativeClusterer clusterer = new HierarchicalAgglomerativeClusterer(this, this, new SingleLinkage());
		clusterer.cluster(dendrogramBuilder);
		Dendrogram dendrogram = dendrogramBuilder.getDendrogram();
		TreeNode root = getTreeNode(dendrogram.getRoot());
		Tree tree = new Tree(root);
		return tree;
	}
	
	private TreeNode getTreeNode(DendrogramNode dendrogramNode)
	{
		TreeNode node = new TreeNode();
		if(dendrogramNode instanceof ObservationNode)
		{
			String id = distancedPoints.getPointId(((ObservationNode)dendrogramNode).getObservation());
			Property<PString> pId = new Property<PString>(PROPERTY_ID, new PString(id));
			node.addProperty(pId);
		}
		else 
		{
			if(dendrogramNode.getRight() != null)
			{
				TreeNode rightTreeNode = getTreeNode(dendrogramNode.getRight());
				node.addChild(rightTreeNode);
			}
			if(dendrogramNode.getLeft() != null)
			{
				TreeNode leftTreeNode = getTreeNode(dendrogramNode.getLeft());
				node.addChild(leftTreeNode);
			}
			
			
		}
		
		
		
		return node;
	}
}
