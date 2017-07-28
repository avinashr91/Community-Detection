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

# Summary

An innovative method was proposed based
on data modeling (indexing) and community detection to
determine a meaningful communities. Enron email dataset was
used here. With graph modeling and clustering first, 11 clusters
were partitioned and in which nodes in the graphs refer to
the users associated in the conversation and the edge between
two users implies that the users have communicated with each
other on a particular conversation (topic). At the second step,
we run modularity optima community detection algorithm on
each topical cluster to test different connection strength. Three
different versions of the modularity maximization method was
used and the experiment results were compared and discussed.
Among the various results a better community partition can
always been chosen to meet different requirements. The performance
obtained in this approach, when compared to the
traditional modularity based method, was more convincing and
meaningful since particular conversation detection plays a role
at least as important as the linkage strength. We believe our approach
has many potential applications in today’s community
detection especially when used in complex social networks.
We are able to find impressive community partition not only
based on various connection strength, but also conversationoriented.
More interesting works, for example, how to improve
the existing algorithm we have to match the state-of-the-art
source, and how to apply our current results on other largerscale
real world datasets, would be our next moving for deeper
understanding.



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
