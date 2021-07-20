import os
import sys
import uuid

#from utils.generator import *
from utils.generator_noise import *
from utils.generator_noise_misc import *
from utils.sinks_sources import *
from utils.greedyKcover import *
from utils.visualization import *
from utils.paths import *
from utils.postprocessing import *
from utils.get_path_stats import *
import pickle

M = 30
N = 100
K = 5
infectionP = 0.3
reportingP = 0.5
recoveringP = 0.0
#K = srcN
#dataset = str(sys.argv[1])
#type = 'ER'
dataset = 'PL'
#dataset = 'facebook'
#dataset = 'twitter'
#dataset = 'students'
#dataset = 'tumblr'
#dataset = 'enron'
#model = 'SI_0-9'
#model = 'SI_1'
#model = 'IC'



#path = os.path.join('CONDOR','shifts_loop','res')
#path = os.path.join('CONDOR', 'shifts_loop', 'res_RG')
path = os.path.join('CONDOR', 'gamma_loop', 'res_more')

print path
onlyfiles = [f for f in listdir(path) if isfile(join(path, f))]
print onlyfiles
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
    gamma_range, correct_found_seeds, cost_ratio, correct_interaction_recall, correct_interaction_precision, \
    correct_causality_recall, correct_causality_precision, total_order_tau, mean_slack, \
    prec_out, prec_lb, prec_ub, recall_out, recall_lb, recall_ub, \
    prec_out_05, prec_lb_05, prec_ub_05, recall_out_05, recall_lb_05, recall_ub_05, \
    F_out, F_lb, F_ub, \
    correct_causality_F, ub_correct_causality_F, correct_interaction_F, ub_correct_interaction_F, \
    mcc_out, mcc_lb, mcc_ub = pickle.load(open(fullname, "rb"))

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


plt.figure('seeds')
plt.xlabel('number of correct seeds')
plt.plot(gamma_range, np.nanmean(arr_correct_found_seeds, axis=0))
plt.xlabel('gamma')
#plt.title('found seeds')
#plt.show()
name = plt.savefig(dataset+'_seeds.pdf')

plt.figure('cost ratio')
#plt.title('cost of found solution / cost of GT solution')
plt.xlabel('gamma')
plt.plot(gamma_range, np.nanmean(arr_cost_ratio, axis=0))
plt.xlabel('gamma')
#plt.show()
name = plt.savefig(dataset + '_cost_ratio.pdf')

plt.figure('correctness_order')
#plt.title('correctness_order')
plt.plot(gamma_range, np.nanmean(arr_correct_interaction_recall, axis=0))
plt.plot(gamma_range, np.nanmean(arr_correct_interaction_precision, axis=0))
plt.plot(gamma_range, np.nanmean(arr_correct_causality_recall, axis=0))
plt.plot(gamma_range, np.nanmean(arr_correct_causality_precision, axis=0))
plt.plot(gamma_range, np.nanmean(arr_total_order_tau, axis=0))
plt.legend(['recall, interactions ', 'precision, interactions', 'recall, order', 'precision, order', 'total order tau'], loc = 3)
plt.xlabel('gamma')
plt.tight_layout()
name = plt.savefig(dataset + '_correctness_order.pdf')

# plt.figure('path_tau_median')
# plt.plot(gamma_range, path_tau_median)
# plt.tight_layout()
# name = plt.savefig('path_tau_median.pdf')
#
# plt.figure('path_len_median')
# plt.plot(gamma_range, path_len_median)
# plt.tight_layout()
# name = plt.savefig('path_len_median.pdf')

plt.figure('mean_slack')
#plt.title('mean slack')
plt.plot(gamma_range, np.nanmean(arr_mean_slack, axis=0))
#plt.ylabel('interactions')
plt.xlabel('gamma')
plt.tight_layout()
name = plt.savefig(dataset+'_mean_slack.pdf')

plt.figure('accuracy_end')
#plt.title('accuracy, by the end')
plt.plot(gamma_range, np.nanmean(arr_prec_out, axis=0), 'k-')
plt.plot(gamma_range, np.nanmean(arr_prec_lb, axis=0), 'k--')
plt.plot(gamma_range, np.nanmean(arr_prec_ub, axis=0), 'k:')
plt.plot(gamma_range, np.nanmean(arr_recall_out, axis=0), 'r-')
plt.plot(gamma_range, np.nanmean(arr_recall_lb, axis=0), 'r--')
plt.plot(gamma_range, np.nanmean(arr_recall_ub, axis=0), 'r:')
plt.legend(['Prec output', 'Prec reports', 'Prec BL', 'Recall output', 'Recall reports', 'Recall BL'], loc = 3)
#plt.ylabel('F1')
plt.xlabel('gamma')
plt.tight_layout()

name = plt.savefig(dataset+'_accuracy.pdf')

plt.figure('accuracy_mid')
#plt.title('accuracy, middle moment')
plt.plot(gamma_range, np.nanmean(arr_prec_out_05, axis=0), 'k-')
plt.plot(gamma_range, np.nanmean(arr_prec_lb_05, axis=0), 'k--')
plt.plot(gamma_range, np.nanmean(arr_prec_ub_05, axis=0), 'k:')
plt.plot(gamma_range, np.nanmean(arr_recall_out_05, axis=0), 'r-')
plt.plot(gamma_range, np.nanmean(arr_recall_lb_05, axis=0), 'r--')
plt.plot(gamma_range, np.nanmean(arr_recall_ub_05, axis=0), 'r:')
plt.legend(['Prec output', 'Prec repotrs', 'Prec BL', 'Recall output', 'Recall reports', 'Recall BL'], loc = 3)
#plt.ylabel('F1')
plt.xlabel('gamma')

plt.tight_layout()
name = plt.savefig(dataset+'_accuracy_05.pdf')

print 'here_05:'
print prec_out_05
print recall_out_05

plt.figure('F1_end')
#plt.title('F1, by the end')
plt.plot(gamma_range, np.nanmean(arr_F_out, axis=0))
plt.plot(gamma_range, np.nanmean(arr_F_lb, axis=0))
plt.plot(gamma_range, np.nanmean(arr_F_ub, axis=0))
plt.legend(['F1', 'F1 reports', 'F1 BL'], loc = 3)
#plt.ylabel('F1')
plt.xlabel('gamma')
plt.tight_layout()
name = plt.savefig(dataset+'_F1.pdf')

plt.figure('order_F1')
#plt.title('compare order, F1')
plt.plot(gamma_range, np.nanmean(arr_correct_causality_F, axis=0))
plt.plot(gamma_range, np.nanmean(arr_ub_correct_causality_F, axis=0))
plt.plot(gamma_range, np.nanmean(arr_correct_interaction_F, axis=0))
plt.plot(gamma_range, np.nanmean(arr_ub_correct_interaction_F, axis=0))
plt.legend(['order output', 'order BL', 'interactions output', 'interactions BL'], loc = 3)
#plt.ylabel('F1')
plt.xlabel('gamma')
plt.tight_layout()
name = plt.savefig(dataset+'_compare order.pdf')


plt.rcParams.update({'font.size': 40, 'lines.linewidth': 3})
plt.rcParams['xtick.labelsize'] =  40
plt.rcParams['ytick.labelsize'] = 40

#print MCC_out, MCC_lb, MCC_ub
plt.figure('MCC_end')
#plt.title('MCC, by the end')


plt.plot(gamma_range, np.nanmean(arr_mcc_out, axis=0))
plt.plot(gamma_range, np.nanmean(arr_mcc_lb, axis=0))
plt.plot(gamma_range, np.nanmean(arr_mcc_ub, axis=0))


plt.legend(['CulT', 'reports', 'baseline'], loc = 4, fontsize = 35)
plt.axvline(x = 3.1, linewidth=2,linestyle = ':', color='k')
#plt.axvline(x = 5.7, linewidth=2, label='TW')
plt.axvline(x = 2.0, linewidth=2, linestyle = ':', color='k')
plt.axvline(x = 2.4, linewidth=2, linestyle = ':', color='k')
plt.axvline(x = 2.4, linewidth=2, linestyle = ':', color='k')

#plt.annotate('Facebook', xy=(3.0, 0.85))
plt.annotate('Facebook', xy=(3.0, 0.7))
#plt.annotate('TW', xy=(5.7, 0.85))
plt.annotate('Students', xy=(1.9, 0.86))
plt.annotate('Tumblr', xy=(2.3, 0.75))
plt.annotate('Enron', xy=(2.3, 0.79))


#labels = ['FB', 'TW', 'ST', 'TM', 'EN']
#plt.xticks([1, 7]+[3.1, 5.7, 2.0, 2.3, 2.5], [1,7]+labels)
#print gamma_range
#plt.xticks(range(1,7))

#plt.ylabel('MCC')
plt.xlabel('gamma')
#plt.tight_layout()
plt.show()


name = plt.savefig(dataset+'_MCC.pdf')
#plt.show()
