library(igraph)

#Get the edge and create the graph
edges <- read.csv("D:/california energy crisis.csv")

G <-graph.data.frame(edges , directed=FALSE)

Sys.time()
## fastgreedy.community
fc <- fastgreedy.community(G)
Sys.time()

fc$vcount

modularity(fc)

#Plot the graph
plot(fc,G)

