import os
import sys
import uuid

# from utils.generator import *
from utils.generator_noise import *
#from utils.generator_noise_misc import *
from utils.sinks_sources import *
from utils.greedyKcover import *
from utils.visualization import *
from utils.paths import *
from utils.postprocessing import *
from utils.get_path_stats import *
import pickle
from os import listdir
from os.path import isfile, join

dataset = 'facebook'
#dataset = 'ER'
#dataset = 'twitter'
#dataset = 'students'
#dataset = 'tumblr'
#dataset = 'enron'
#model = 'SI_0-9'
model = 'SI_1'
model = 'IC'
#model = 'SP'
notmodel = 'SI_1__'

plt.rcParams.update({'font.size': 15, 'lines.linewidth': 3})
plt.rcParams['xtick.labelsize'] = 27
plt.rcParams['ytick.labelsize'] = 27

#path = os.path.join('CONDOR', 'noise_loop_more_noise', 'res')
path = os.path.join('CONDOR', 'noise_loop_more', 'res_1000_IC')
print listdir(path)
onlyfiles = [f for f in listdir(path) if isfile(join(path, f)) and dataset in f and model in f and notmodel not in f]
print onlyfiles
#onlyfiles = ['facebook_SI_1_eac07efe-a643-40fa-9277-7cde5c43487c.p']
max_iter = len(onlyfiles)



arr_correct_found_seeds = [0]*max_iter
arr_cost_ratio = [0]*max_iter
arr_correct_interaction_recall = [0]*max_iter
arr_correct_interaction_precision = [0]*max_iter
arr_correct_causality_recall = [0]*max_iter
arr_correct_causality_precision = [0]*max_iter
arr_total_order_tau = [0]*max_iter
arr_mean_slack = [0]*max_iter
arr_prec_out = [0]*max_iter
arr_prec_lb = [0]*max_iter
arr_prec_ub = [0]*max_iter
arr_recall_out = [0]*max_iter
arr_recall_lb = [0]*max_iter
arr_recall_ub = [0]*max_iter
arr_prec_out_05 = [0]*max_iter
arr_prec_lb_05 = [0]*max_iter
arr_prec_ub_05 = [0]*max_iter
arr_recall_out_05 = [0]*max_iter
arr_recall_lb_05 = [0]*max_iter
arr_recall_ub_05 = [0]*max_iter
arr_F_out = [0]*max_iter
arr_F_lb = [0]*max_iter
arr_F_ub = [0]*max_iter
arr_correct_causality_F = [0]*max_iter
arr_ub_correct_causality_F = [0]*max_iter
arr_correct_interaction_F = [0]*max_iter
arr_ub_correct_interaction_F = [0]*max_iter
arr_mcc_out = [0]*max_iter
arr_mcc_lb = [0]*max_iter
arr_mcc_ub = [0]*max_iter


iter = 0

for f in onlyfiles:
    fullname = os.path.join(path, f)
    print fullname
    noise_range, correct_found_seeds, cost_ratio, correct_interaction_recall, correct_interaction_precision, \
    correct_causality_recall, correct_causality_precision, total_order_tau, mean_slack, \
    prec_out, prec_lb, prec_ub, recall_out, recall_lb, recall_ub, \
    prec_out_05, prec_lb_05, prec_ub_05, recall_out_05, recall_lb_05, recall_ub_05, \
    F_out, F_lb, F_ub, \
    correct_causality_F, ub_correct_causality_F, correct_interaction_F, ub_correct_interaction_F, \
    mcc_out, mcc_lb, mcc_ub,t1,t2  = pickle.load(open(fullname, "rb"))

    arr_correct_found_seeds[iter] = correct_found_seeds
    arr_cost_ratio[iter] = cost_ratio
    arr_correct_interaction_recall[iter] = correct_interaction_recall
    arr_correct_interaction_precision[iter] = correct_interaction_precision
    arr_correct_causality_recall[iter] = correct_causality_recall
    arr_correct_causality_precision[iter] = correct_causality_precision
    arr_total_order_tau[iter] = total_order_tau
    arr_mean_slack[iter] = mean_slack
    arr_prec_out[iter] = prec_out
    arr_prec_lb[iter] = prec_lb
    arr_prec_ub[iter] = prec_ub
    arr_recall_out[iter] = recall_out
    arr_recall_lb[iter] = recall_lb
    arr_recall_ub[iter] = recall_ub
    arr_prec_out_05[iter] = prec_out_05
    arr_prec_lb_05[iter] = prec_lb_05
    arr_prec_ub_05[iter] = prec_ub_05
    arr_recall_out_05[iter] = recall_out_05
    arr_recall_lb_05[iter] = recall_lb_05
    arr_recall_ub_05[iter] = recall_ub_05
    arr_F_out[iter] = F_out
    arr_F_lb[iter] = F_lb
    arr_F_ub[iter] = F_ub
    arr_correct_causality_F[iter] = correct_causality_F
    arr_ub_correct_causality_F[iter] = ub_correct_causality_F
    arr_correct_interaction_F[iter] = correct_interaction_F
    arr_ub_correct_interaction_F[iter] = ub_correct_interaction_F
    arr_mcc_out[iter] = mcc_out
    arr_mcc_lb[iter] = mcc_lb
    arr_mcc_ub[iter] = mcc_ub

    iter += 1

# unique_out = dataset + '_' + str(uuid.uuid4())
# pickle.dump([noise_range, correct_found_seeds, cost_ratio, correct_interaction_recall, correct_interaction_precision,
#              correct_causality_recall, correct_causality_precision, total_order_tau, mean_slack,
#              prec_out, prec_lb, prec_ub, recall_out, recall_lb, recall_ub,
#              prec_out_05, prec_lb_05, prec_ub_05, recall_out_05, recall_lb_05, recall_ub_05,
#              F_out, F_lb, F_ub,
#              correct_causality_F, ub_correct_causality_F, correct_interaction_F, ub_correct_interaction_F,
#              mcc_out, mcc_lb, mcc_ub], open(unique_out+".p", "wb"))
# exit()
#dataset = dataset +'_'+ model

#noise_range = [50.0/(n*50.0+50.0) for n in noise_range]
#noise_range = [1.0/(n*1.0+1.0) for n in noise_range]
noise_range = [1.0/(n+1.0) for n in noise_range]
#noise_range = []+[1.0/n for n in noise_range[1:]]

plt.figure('seeds')
plt.xlabel('number of correct seeds')
plt.plot(noise_range, np.nanmean(arr_correct_found_seeds, axis=0))
plt.xlabel('fraction of relevant interactions', fontsize=27)
plt.yscale('log')
#plt.title('found seeds')
#plt.show()
plt.ylim([0, 1.1])
name = plt.savefig(dataset + '_seeds.pdf')

plt.figure('cost ratio')
#plt.title('cost of found solution / cost of GT solution')
plt.xlabel('fraction of relevant interactions')
plt.plot(noise_range, np.nanmean(arr_cost_ratio, axis=0))
plt.ylim([0, 1.1])
plt.xlim([min(noise_range), 1.0])
#plt.xscale('log')
plt.xlabel('fraction of relevant interactions', fontsize=27)

#plt.show()
name = plt.savefig(dataset + '_cost_ratio.pdf')

plt.figure('correctness_order')
#plt.title('correctness_order')
plt.plot(noise_range, np.nanmean(arr_correct_interaction_recall, axis=0))
plt.plot(noise_range, np.nanmean(arr_correct_interaction_precision, axis=0))
plt.plot(noise_range, np.nanmean(arr_correct_causality_recall, axis=0))
plt.plot(noise_range, np.nanmean(arr_correct_causality_precision, axis=0))
plt.plot(noise_range, np.nanmean(arr_total_order_tau, axis=0))
plt.legend(['recall, interactions ', 'precision, interactions', 'recall, order', 'precision, order', 'total order tau'],
           loc=4)
plt.xlabel('fraction of relevant interactions')
plt.tight_layout()
plt.ylim([0, 1.1])
plt.xlim([min(noise_range), 1.0])
plt.xscale('log')
plt.xlabel('fraction of relevant interactions')
name = plt.savefig(dataset + '_correctness_order.pdf')

# plt.figure('path_tau_median')
# plt.plot(noise_range, path_tau_median)
# plt.tight_layout()
# name = plt.savefig('path_tau_median.pdf')
#
# plt.figure('path_len_median')
# plt.plot(noise_range, path_len_median)
# plt.tight_layout()
# name = plt.savefig('path_len_median.pdf')

plt.figure('mean_slack')
##plt.title('mean slack')
plt.plot(noise_range, np.nanmean(arr_mean_slack, axis=0))
#plt.ylabel('interactions')
plt.xlabel('fraction of relevant interactions', fontsize=27)
plt.tight_layout()
#plt.ylim([0, 1.1])
plt.xlim([min(noise_range), 1.0])
#plt.xscale('log')
name = plt.savefig(dataset + '_mean_slack.pdf')

plt.figure('accuracy_end')
##plt.title('accuracy, by the end')
plt.plot(noise_range, np.nanmean(arr_prec_out, axis=0), 'k-')
plt.plot(noise_range, np.nanmean(arr_prec_lb, axis=0), 'k--')
plt.plot(noise_range, np.nanmean(arr_prec_ub, axis=0), 'k:')
plt.plot(noise_range, np.nanmean(arr_recall_out, axis=0), 'r-')
plt.plot(noise_range, np.nanmean(arr_recall_lb, axis=0), 'r--')
plt.plot(noise_range, np.nanmean(arr_recall_ub, axis=0), 'r:')
plt.legend(['Prec output', 'Prec reports', 'Prec BL', 'Recall output', 'Recall reports', 'Recall BL'], loc=4)
#plt.ylabel('F1')
plt.ylim([0, 1.1])
plt.xlim([min(noise_range), 1.0])
plt.xscale('log')
plt.xlabel('fraction of relevant interactions')
plt.tight_layout()
plt.xlim([min(noise_range), 1.0])

name = plt.savefig(dataset + '_accuracy.pdf')

plt.figure('accuracy_mid')
##plt.title('accuracy, middle moment')
plt.plot(noise_range, np.nanmean(arr_prec_out_05, axis=0), 'k-')
plt.plot(noise_range, np.nanmean(arr_prec_lb_05, axis=0), 'k--')
plt.plot(noise_range, np.nanmean(arr_prec_ub_05, axis=0), 'k:')
plt.plot(noise_range, np.nanmean(arr_recall_out_05, axis=0), 'r-')
plt.plot(noise_range, np.nanmean(arr_recall_lb_05, axis=0), 'r--')
plt.plot(noise_range, np.nanmean(arr_recall_ub_05, axis=0), 'r:')
plt.legend(['Prec output', 'Prec repotrs', 'Prec BL', 'Recall output', 'Recall reports', 'Recall BL'], loc=4)
#plt.ylabel('F1')
plt.xlabel('fraction of relevant interactions')

plt.ylim([0, 1.1])
plt.xlim([min(noise_range), 1.0])
plt.xscale('log')
plt.tight_layout()
name = plt.savefig(dataset + '_accuracy_05.pdf')

print 'here_05:'
print prec_out_05
print recall_out_05

plt.figure('F1_end')
##plt.title('F1, by the end')
plt.plot(noise_range, np.nanmean(arr_F_out, axis=0))
plt.plot(noise_range, np.nanmean(arr_F_lb, axis=0))
plt.plot(noise_range, np.nanmean(arr_F_ub, axis=0))
plt.legend(['F1', 'F1 reports', 'F1 BL'], loc=4)
#plt.ylabel('F1')
plt.xlabel('fraction of relevant interactions')
plt.tight_layout()
plt.ylim([0, 1.1])
plt.xlim([min(noise_range), 1.0])
plt.xscale('log')
name = plt.savefig(dataset + '_F1.pdf')

plt.figure('order_F1')
##plt.title('compare order, F1')
plt.plot(noise_range, np.nanmean(arr_correct_causality_F, axis=0))
plt.plot(noise_range, np.nanmean(arr_ub_correct_causality_F, axis=0))
plt.plot(noise_range, np.nanmean(arr_correct_interaction_F, axis=0))
plt.plot(noise_range, np.nanmean(arr_ub_correct_interaction_F, axis=0))
plt.legend(['order output', 'order BL', 'interactions output', 'interactions BL'], loc=4)
#plt.ylabel('F1')
plt.xlabel('fraction of relevant interactions')
plt.tight_layout()
plt.ylim([0, 1.1])
plt.xlim([min(noise_range), 1.0])
plt.xscale('log')
name = plt.savefig(dataset + '_compare order.pdf')

#print MCC_out, MCC_lb, MCC_ub
plt.figure('MCC_end')
##plt.title('MCC, by the end')
plt.plot(noise_range, np.nanmean(arr_mcc_out, axis=0))
plt.plot(noise_range, np.nanmean(arr_mcc_lb, axis=0))
plt.plot(noise_range, np.nanmean(arr_mcc_ub, axis=0))
#plt.legend(['CulT', 'reports', 'baseline'], loc=4 , fontsize=25)
#plt.ylabel('MCC')
plt.xlabel('fraction of relevant interactions', fontsize=30)
plt.tight_layout()
plt.ylim([0.3, 1.0])
plt.xlim([min(noise_range), 1.0])
plt.xscale('log')
name = plt.savefig(dataset + '_MCC.pdf')
#plt.show()
