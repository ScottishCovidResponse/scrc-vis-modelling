
import pandas as pd
import numpy as np
import re
import networkx as nx
import matplotlib.pyplot as plt

""" This is a processing script that contains a set of functions to read in 
an infection map file and generate an internal networkx graph which can then 
be exported to viz-friendly formats such as graphml. """

def removeWhiteSpace(textToParse):
    """This removes the whitespaces from a given piece of string"""
    return "".join(textToParse.split());


def parseNodeTimeObject(nodeText):
    """Takes in an individual node text and separates them into the Node-ID and infection time"""
    nodeText = removeWhiteSpace(nodeText);
    x = re.split("\(", nodeText);
    nodeID = x[0];
    time = re.split("\)", x[1])[0];
    return (nodeID, time);

def parseInfectedNodesList(textToParse):
    """Parses a list of infected nodes text and returns a list of nodes as a Python list"""
    textToParse = removeWhiteSpace(textToParse);
    infectedNodes = re.split(',', re.split('\]$', re.split('^\[', textToParse)[1])[0]);
    return infectedNodes;



def convertInfectionMapToNetworkXGraph(infectionMapFileName):
    '''Reads in an infection map file and returns a directed NetworkX graph object'''

    # This is a directed graph object to hold all the infection chains
    G = nx.DiGraph()

    # This is the name of the attribute that each node/edge has, currently only infection time
    attributeText1 = "infectionTime"

    with open(infectionMapFileName) as f:
        #read_data = f.read()
        lines = f.readlines()
        #print("file", f)
        for line in lines:
            if(line == '\n'):
                pass;
                #print ("This is an empty line!");
            else:
                #print(line);
                # First check how many "->" this infection line has.

                tempSplit = re.split("->", line)
                if len(tempSplit) == 2:
                    #This is the initial phase in the chain
                    tempSourceNode = parseNodeTimeObject(tempSplit[0]);
                    #print("SourceNode:", tempSourceNode);
                    G.add_node(tempSourceNode[0], infectionTime = int(tempSourceNode[1]))
                    tempTargetNodes = parseInfectedNodesList(tempSplit[1]);
                    #print("TargetNodes:", tempTargetNodes);
                    for nodeText in tempTargetNodes:
                        tempTargetNode = parseNodeTimeObject(nodeText);
                        #print("Adding TargetNode:", tempTargetNode);
                        G.add_node(tempTargetNode[0], infectionTime=int(tempTargetNode[1]))
                        G.add_edge(tempSourceNode[0], tempTargetNode[0], infectionTime = int(tempSourceNode[1]))
                else:
                    #This is further down the chain, if we are here, the source node should be already in the graph.

                    #print("This is inside the Infection Chain");
                    tempSourceNode = parseNodeTimeObject(tempSplit[1]);
                    #print("SourceNode:", tempSourceNode, " -- this should be already in the graph, not adding");

                    tempTargetNodes = parseInfectedNodesList(tempSplit[2]);
                    #print("TargetNodes:", tempTargetNodes);
                    for nodeText in tempTargetNodes:
                        tempTargetNode = parseNodeTimeObject(nodeText);
                        #print("Adding TargetNode:", tempTargetNode);
                        G.add_node(tempTargetNode[0], infectionTime=int(tempTargetNode[1]))
                        G.add_edge(tempSourceNode[0], tempTargetNode[0], infectionTime=int(tempSourceNode[1]))
    return G;


### Here is a testing routine that draws the graph and generates a graphml export

G = convertInfectionMapToNetworkXGraph('infectionMap_sample.txt')
nodes = G.nodes(data=True)
plt.figure(1,figsize=(12,12))
colors = [v['infectionTime'] for u,v in nodes]
#print(colors)
pos = nx.spring_layout(G)
nx.draw(G, pos=pos, cmap=plt.cm.Blues, node_color= colors)
nx.draw_networkx_labels(G, pos=pos, font_size = 8)
plt.show()

nx.write_graphml_lxml(G, "fullInfectionMap.graphml")