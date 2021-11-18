from datetime import datetime, timedelta
import networkx as nx
from utils.accuracy import *


def get_sinks_and_sources(contacts,NS):
    immuned, sinks, sources, unreported = {}, {}, {}, {}

    default = contacts[-1][0]

    nodes = set()
    for iter in range(len(NS)):
            node_report = NS[iter]
            n,t = node_report[0], node_report[1]
            assert(n not in sources) #it is double in the list
        
            nodes.add(n)
            if(t != ""):
                sources[n] = t
                sinks[n] = t
            else:
                unreported[n] = default #set timestamp equal to last timestamp.
    
    return sources, immuned, sinks, unreported