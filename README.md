# Community-Detection

Abstract—Discover and analysis of the community in a complex 
graph is one of the topic interests within recent years. It can be
used as a potent tool in understanding the structure of a complex,
large graph, for example, the social network in nowadays. With
a good community detection in social networks, we are able to
improve the efficiency of collaboration, knowledge and information
sharing, and some other applications which can benefit
our lives greatly. Most existing methods are mainly focus on the
community detection based on the linkage analysis but without
the semantic reflection. To address this problem, we aim to find
communities based on conversations in one real dataset, Enron
email corpus (http://www.cs.cmu.edu/∼enron/). Here we define
the “conversation” based on the topic of discussion in a given
mail thread where nodes represent users and edges represent
conversations between users. Community detection in this work
will allow us to find set of users who are not only have the closed
connection but also share the same topic, that is, find the users
who are strongly connected based on conversation. To achieve a
good performance, the email users are clustered based on various
email topics and several community detection algorithms were
run on each cluster to test link strength among users. Experiment
results were concluded and compared. The approach used in this
work can effectively extract community structure representing
users based on their conversation frequency in a given topic.


# Working of the Project: 

SOURCE CODE folder has 3 subfolders:
	
	a. Data Modelling - This contains the files used for preprossing and indexing. 
	The program can be started by calling Indexing.jar
	
	NOTE :  The input for this data(compresessed version) was about 1 GB in size.
	
	b. Community Detection - This folder contains Program.jar and contains our implementation of Fast Modulartiy approach.
	
	c. Graph Visualization - This folder contains the the R code for community detection from igraph package along with graph visualization module for analyzing the communities.
	
	NOTE : R is an iterative programming language and hence no executables can be given for this.
    	R must be set up to execute this.
		
	Please reach out for any clarifications regarding code or the exectuion.
