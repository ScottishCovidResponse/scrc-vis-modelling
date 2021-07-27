__author__ = 'Polina'
import os.path
import copy
import networkx as nx
import matplotlib.pyplot as plt
import numpy as np
import operator
import scipy.stats as stats
from collections import defaultdict

def get_GT_cost(TS, sources, sinks, unreported):
    infected = set()
    cost = 0.0
    interactions = []
    causality = []
    for i in TS:
        t, n1, n2, inf1, inf2, rep1, rep2, ext1, ext2 = i[0], i[1], i[2], i[3], i[4], i[5], i[6], i[7], i[8]
        if inf1 == 1:
            infected.add(n1)
            if n2 not in infected and inf2 == 1:
                penalty_n1 = 0.5*np.abs((sources[n1]-t)) if n1 in sinks else 0.5*np.abs((unreported[n1]-t))
                penalty_n2 = 0.5*np.abs((sources[n2]-t)) if n2 in sinks else 0.5*np.abs((unreported[n2]-t))
                cost += penalty_n1 + penalty_n2
                interactions.append((t, n1, n2))
                infected.add(n2)
                causality.append((n1, n2))

    return cost, interactions, causality

def get_out_cost(output_paths, sources, sinks, unreported):
    cost = 0.0
    interactions = []
    causality = []
    for p in output_paths:
        for step in p:
            t, n1, n2 = step[0], step[1], step[2]
            if n1 != n2 and step not in interactions:
                interactions.append(step)
                causality.append((n1, n2))
                penalty_n1 = 0.5*np.abs((sources[n1]-t)) if n1 in sinks else 0.5*np.abs((unreported[n1]-t))
                penalty_n2 = 0.5*np.abs((sources[n2]-t)) if n2 in sinks else 0.5*np.abs((unreported[n2]-t))
                cost += penalty_n1 + penalty_n2
    return cost, interactions, causality