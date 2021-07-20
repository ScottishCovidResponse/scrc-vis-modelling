import os

from utils.generator_noise import *
# from utils.visualization import *
from utils.sinks_sources import *
from utils.greedyKcover import *
from utils.get_path_stats import *
import pickle

srcN = 9
reportingP = 0.5
K = srcN
real = True

dataset = 'flixter_1000_K9'
filepath = "F:/Development/Swansea/scrc-vis-modelling/ContactTracing/GraphVisualisation/RepresentativeTreesViz/RepresentativeTreeCalculator/reconstructing-an-epidemic-over-time/Data/flixter_1000_K9.txt";
TS, G, infection_order, infection_track, seeds = readFile(filepath, mode='general')

print (len(TS),len(seeds),len(infection_order),len(infection_track))

dt_sec = 100
sources, immuned, sinks, reported, unreported, sources_TnI, sinks_TnI, unreported_TnI = get_sinks_and_sources_shifted(
    TS, G=nx.Graph(), mode='all', dt_sec=dt_sec, rep_prob=reportingP)
print (len(sinks), len(sources), len(reported['infected']))
#exit()
#SP = shortestPath1(TS, sources, sinks, immuned, unreported)
SP = shortestPath1(TS, sources, sinks, immuned, unreported)
#K = srcN

cover, output_paths, cover_cost, legal_alpha = greedyBS(SP, len(sinks), K)
gt_cost, gt_interactions, gt_causality = get_GT_cost(TS, sources, sinks, unreported)
print ('cost of GT', gt_cost)
print (cover)
print (output_paths)

out_cost, out_interactions, out_causality = get_out_cost(output_paths, sources, sinks, unreported)
print ('cost of our solution', out_cost)
#exit()

print ('correct interactions', len(set(gt_interactions).intersection(set(out_interactions))))
print ('intesection precision', np.divide(1.0 * len(set(gt_interactions).intersection(set(out_interactions))),
                                         len(set(gt_interactions))))
print ('intesection recall', np.divide(1.0 * len(set(gt_interactions).intersection(set(out_interactions))),
                                      len(set(out_interactions))))

print ('correct of causality', len(set(gt_causality).intersection(set(out_causality))))
print ('causality precision', np.divide(1.0 * len(set(gt_causality).intersection(set(out_causality))),
                                       len(set(gt_causality))))
print ('causality recall', np.divide(1.0 * len(set(gt_causality).intersection(set(out_causality))),
                                    len(set(out_causality))))
#exit()
#get_infection_paths_noise(TS, output_paths, cover.keys())
N = G.number_of_nodes()
M = len(TS)

ticksN = 100
GT_snapshots, moment_of_infection, _, _ = get_snapshots(TS, ticksN, N)

#path_tau, path_lengths = validatePaths(output_paths, moment_of_infection, set(sinks.keys()))
#print 'path-wise tau', np.nanmean(path_tau), np.nanmedian(path_tau)
#print 'path length', np.nanmean(path_lengths), np.nanmedian(path_lengths)
#exit()


#folder = time.strftime('test_' + type + "_%Y%m%d-%H%M%S")
#os.mkdir(folder)

#added_infections, counters, gt_uninf_neighbors, out_uninf_neighbors, gt_inf_neighbors, out_inf_neighbors = postPr(TS, output_paths, 0.3)

print ('accuracy')
draw = False
ticksN = 100

lb_snapshots = get_lb_snapshots(sources, immuned, reported, GT_snapshots)
#ub_snapshots_cascade = get_ub_snapshots_cascade(TS, GT_snapshots, sinks)
ub_snapshots_cascade, ub_interactions, ub_causality = get_ub_snapshots(TS, GT_snapshots, sinks)

print ('intesection precision ub', np.divide(1.0 * len(set(gt_interactions).intersection(set(ub_interactions))),
                                            len(set(gt_interactions))))
print ('intesection recall ub', np.divide(1.0 * len(set(gt_interactions).intersection(set(ub_interactions))),
                                         len(set(ub_interactions))))

print ('causality precision ub', np.divide(1.0 * len(set(gt_causality).intersection(set(ub_causality))),
                                          len(set(gt_causality))))
print ('causality recall ub', np.divide(1.0 * len(set(gt_causality).intersection(set(ub_causality))),
                                       len(set(ub_causality))))

output_snapshots, found_seeds = get_output_snapshots_no_recov_pred(output_paths, GT_snapshots, immuned)
print (set(found_seeds.keys()).intersection(set(seeds)))

print ('how far in time')
slack, tau = how_far_intime(output_paths, moment_of_infection)
print ('total tau', tau)
print ('slack', np.nanmean(slack),np.nanmedian(slack))

folder = ''
pred_recover = False
prec_infected, recall_infected, prec_recovered, recall_recovered, abs_values_tp, gt_positive, abs_values_p, set_nodes, set_nodes_gt, MCC, F1 \
    = snapshot_accuracy(GT_snapshots, output_snapshots, output_paths, immuned, sources, reported, TS, G, N, 'main',
                        pred_recover=pred_recover, draw=draw, folder=folder)
prec_lb_infected, recall_lb_infected, prec_lb_recovered, recall_lb_recovered, abs_values_lb_tp, _, _, _, _, MCC_lb, F1_lb \
    = snapshot_accuracy(GT_snapshots, lb_snapshots, output_paths, immuned, sources, reported, TS, G, N, 'lb',
                        pred_recover=pred_recover, draw=draw, folder=folder)
prec_ub_infected, recall_ub_infected, prec_ub_recovered, recall_ub_recovered, abs_values_ub_tp, _, abs_values_ub_p, _, _, MCC_ub, F1_ub \
    = snapshot_accuracy(GT_snapshots, ub_snapshots_cascade, output_paths, immuned, sources, reported, TS, G, N, 'ub',
                        pred_recover=pred_recover, draw=draw, folder=folder)
degrees_out = [G.degree(i) for i in set_nodes[-1]]
degrees_gt = [G.degree(i) for i in set_nodes_gt[-1]]

print (np.nanmedian(prec_infected),np.nanmedian(recall_infected), np.nanmedian(F1))
print (np.nanmedian(prec_lb_infected), np.nanmedian(recall_lb_infected), np.nanmedian(F1_lb))
print (np.nanmedian(prec_ub_infected), np.nanmedian(recall_ub_infected), np.nanmedian(F1_ub))

unique_out = str(reportingP).replace('.', '-') + '_' + dataset + '_' + str(uuid.uuid4())

title = 'accuracy'
plt.figure('accuracy')
plt.plot(prec_infected, 'k-')
plt.plot(prec_lb_infected, 'k--')
plt.plot(prec_ub_infected, 'k:')
plt.plot(recall_infected, 'r-')
plt.plot(recall_lb_infected, 'r--')
plt.plot(recall_ub_infected, 'r:')
plt.xlabel('snapshots')
plt.title('accuracy')
plt.ylim(ymax=1.01, ymin=-0.1)
plt.legend(['Prec CulT', 'Prec reports', 'Prec BL', 'Recall CulT', 'Recall reports', 'Recall BL'], loc=3)
#plt.title(title)
name = dataset + '_acc' + '.pdf'
plt.tight_layout()
name = plt.savefig(name)
plt.show()
