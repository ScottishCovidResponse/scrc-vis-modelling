from datetime import datetime, timedelta
import networkx as nx
from utils.accuracy import *


def get_sinks_and_sources(TS, mode = 'reported'):
    immuned, sinks, sources, reported, unreported = {}, {}, {}, {}, {}
    sinks_TnI, sources_TnI, unreported_TnI = {}, {}, {}
    reported['infected'] = set()
    reported['recovered'] = set()

    default, default_itr = TS[-1][0], len(TS)-1

    nodes = set()
    for iter in range(len(TS)):
        interaction = TS[iter]
        t, n1, n2, status1, report = interaction[0], interaction[1], interaction[2], interaction[3], interaction[5]
        nodes.add(n1)
        nodes.add(n2)
        if report == 1:
            if status1 == 1 and (n1 not in sources or sources[n1] == default):
                sources[n1] = t
                sources_TnI[n1] = (t, iter)
                sinks[n1] = t
                sinks_TnI[n1] = (t,iter)
                reported['infected'].add(n1)
            elif status1 == -1 and n1 not in immuned:
                immuned[n1] = t
                reported['recovered'].add(n1)
        #elif mode == 'all':
            #default = TS[-1][0]
        #    sources[n1] = sources.get(n1, default)

    #mark all nodes not reported as unreported
    for n in nodes:
        if n not in sources:
            unreported[n] = default #set timestamp equal to last timestamp.
            unreported_TnI[n] = (default, default_itr)
            if mode == 'all':
                sources[n] = sources.get(n, default)

    return sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI
