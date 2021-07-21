import os

from utils.generator_noise import *
from utils.sinks_sources import *
from utils.greedyKcover import *
from utils.get_path_stats import *
import pickle

srcN = 9
reportingP = 0.5
K = srcN
real = True
dataset = 'flixter_1000_K9'
filepath = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructingEpidemic/Data/flixter_1000_K9.txt";
TS, G, infection_order, infection_track, seeds = readFile(filepath)

#G is a directed graph containing an edge between n1 and n2 if there is a contact between n1 and n2 in the data
#TS contains a array of arrays of the inputdata. [time,id1,id2,status1,status2,report1,report2]
#Infection_order is ???? Weird as hell and always known?
#Infection track is ???? No idea
#Seeds is the initial infections


dt_sec = 100

#TODO: Change to non-shifted variant
sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI = get_sinks_and_sources_shifted(
    TS, G=nx.Graph(), mode='all', dt_sec=dt_sec, rep_prob=reportingP)

SP = shortestPath1(TS, sources, sinks, immuned, unreported)

cover, output_paths, cover_cost, legal_alpha = greedyBS(SP, len(sinks), K)
gt_cost, gt_interactions, gt_causality = get_GT_cost(TS, sources, sinks, unreported)
print ('cost of GT', gt_cost)
print (cover)
print (output_paths)

out_cost, out_interactions, out_causality = get_out_cost(output_paths, sources, sinks, unreported)
print ('cost of our solution', out_cost)
#exit()

N = G.number_of_nodes()

ticksN = 100
GT_snapshots, moment_of_infection, _, _ = get_snapshots(TS, ticksN, N)

#added_infections, counters, gt_uninf_neighbors, out_uninf_neighbors, gt_inf_neighbors, out_inf_neighbors = postPr(TS, output_paths, 0.3)
print ('accuracy')
draw = False
ticksN = 100

output_snapshots, found_seeds = get_output_snapshots_no_recov_pred(output_paths, GT_snapshots, immuned)
print (set(found_seeds.keys()).intersection(set(seeds)))

folder = ''
pred_recover = False
prec_infected, recall_infected, prec_recovered, recall_recovered, abs_values_tp, gt_positive, abs_values_p, set_nodes, set_nodes_gt, MCC, F1 \
    = snapshot_accuracy(GT_snapshots, output_snapshots, output_paths, immuned, sources, reported, TS, G, N, 'main',
                        pred_recover=pred_recover, draw=draw, folder=folder)

title = 'accuracy'
plt.figure('accuracy')
plt.plot(prec_infected, 'k-')
plt.plot(recall_infected, 'r-')
plt.xlabel('snapshots')
plt.title('accuracy')
plt.ylim(ymax=1.01, ymin=-0.1)
plt.legend(['Prec CulT', 'Recall CulT'], loc=3)
#plt.title(title)
name = dataset + '_acc' + '.pdf'
plt.tight_layout()
name = plt.savefig(name)
plt.show()
