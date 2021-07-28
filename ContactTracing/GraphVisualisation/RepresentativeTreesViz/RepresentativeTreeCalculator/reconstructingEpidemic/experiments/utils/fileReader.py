__author__ = 'Polina'
from datetime import datetime


# Input format edges
# time of transaction(unix timestamp), n1Id, n2Id, weight
#
# Input format nodes
# n1Id, report time(unix timestamp)
def readRealFiles(filepath_edges, filepath_nodes):
    TS = []
    NS = []
    with open(filepath_edges, 'r') as fd:
        for line in fd.readlines():
            line = line.strip()
            items = line.split('\t')

            tstamp = int(items[0])
            n1, n2 = int(items[1]), int(items[2])
            weight = float(items[3])

            if n1 == n2:
                continue
            TS.append([tstamp] + [n1, n2] + [weight])

    fd.close()

    with open(filepath_nodes, 'r') as fd:
        for line in fd.readlines():
            line = line.strip()
            items = line.split('\t')

            n1 = int(items[0])
            if(len(items) == 2):
                tstamp = int(items[1])
            else:
                tstamp = ""
            NS.append([n1]+[tstamp])

    fd.close()

    return TS, NS
