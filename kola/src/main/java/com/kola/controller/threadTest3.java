//package com.kola.controller;
//
//import java.io.File;
//import java.util.*;
//import java.util.concurrent.*;
//
//import org.apache.commons.io.FilenameUtils;
//
//import DAO.SqlMapper;
//import DAO.User;
//import global.Contants;
//import service.processCardId;
//import tool.CSVUtil;
//import tool.Mylog;
//import tool.Myspring;
//
//
//// CallableTest
//public class threadTest3 {
//
//	private static String resultFileName = "result.txt";
//	private static String suffix = "_result.csv";
//	private static int threadNum = 5;	//�߳���
//
//    public static void main(String[] args) throws Exception {
//
//		String filePath = "C:/Users/wangtiantian/Desktop/cardId_yxy/test.csv";
//		String filename = "test.csv";
//		File file = new File(filePath);
//		List<String[]> dataList = CSVUtil.readCSV(file);
//
//    	Mylog.info("ʹ�� Callable ��÷��ؽ����");
//
//        long start = System.currentTimeMillis();
//     	String basename = FilenameUtils.getBaseName(filename);
// 		String recordFilename = basename + suffix;
// 		Mylog.info("recordCSV filename: " + recordFilename);
//
//        dataList.remove(dataList.get(0));
//
//		SqlMapper sqlMapper =  Myspring.getDBBean();
//		List<String> accountList = sqlMapper.selectCardIdFromUser();
//
//        List<FutureTask<List<String[]>>> futureTasks = new ArrayList<>(threadNum);
//        // �½� 5 ���߳�
//        int dataSize = dataList.size();	// ����������
//        int threadSize = dataSize / threadNum;	//ÿ���̴߳���������
//        if (dataSize % threadNum != 0) {
//        	threadNum += 1;
//        }
//        Mylog.info("threadNum: " + threadNum);
//        Mylog.info("threadSize: " + threadSize);
//
//        List<String[]> cutList = new ArrayList<String[]>();
//        final List<String> finalAccunList = accountList;
//        for (int i = 0; i < threadNum; i++) {
//            if (i == threadNum - 1) {
//            	threadSize = dataSize - (i * threadSize);
//            }
//            cutList = dataList.subList(0, threadSize);
//            Mylog.info("��" + (i + 1) + "�飺" + cutList.toString());
//
//            final List<String[]> listStr = cutList;
//            Mylog.info("listStr size1:" + listStr.size());
//
//        	processCallable task = new processCallable(dataList, finalAccunList);
//            FutureTask<List<String[]>> futureTask = new FutureTask<>(task);
//            futureTasks.add(futureTask);
//
//            Thread worker = new Thread(futureTask, "�߳�" + i);
//            worker.start();
//
//            cutList.clear();
//        }
//
//        List<String[]> wrongDataList = new ArrayList<>();
//    	wrongDataList.add(new String[] {"�˺�/AccountNumber", "����/CardId", "������ϸ/Error details"});
//
//        for (FutureTask<List<String[]>> futureTask : futureTasks) {
//        	wrongDataList.addAll(futureTask.get()) ; // get() ����������ֱ����ý��
//        }
//
//        Mylog.info("wrongDataList size: " + wrongDataList.size());
//
//		String folderPath = processCardId.readProperties();
//		Mylog.info("folderPath: " + folderPath);
//
//        String batchUpdateRsult = "SUCCESS|" + basename;
//        if (wrongDataList.size() > 1) {
//        	batchUpdateRsult = "FAIL|" + basename + "|" + recordFilename;	// result|filename|filename.csv
//
//        	// ��¼��������
//        	String recordFilePath = folderPath + File.separator + recordFilename;		// errRecordFile
//        	Mylog.info("recordFilePath: " + recordFilePath);
//
//        	try {
//    			CSVUtil.createCSV(wrongDataList, recordFilePath);
//
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    			Mylog.err(e.getMessage());
//    		}
//        }
//
//        // ��¼���
//        String resultFilePath = folderPath + File.separator + resultFileName;		// 2esult.txt
//        Mylog.info("resultFilePath: " + resultFilePath);
//
//        processCardId.writeTxt(batchUpdateRsult, resultFilePath);
//    }
//
//    static final class processCallable implements Callable<List<String[]>> {
//
//        private final List<String[]> dataList;
//        private final List<String> accountList;
//
//        public processCallable(List<String[]> dataList, List<String> accountList) {
//            this.dataList = dataList;
//            this.accountList = accountList;
//        }
//
//        @Override
//        public List<String[]> call() throws Exception {
//        	List<String[]> result = new ArrayList<String[]>();
//
//        	List<String[]> wrongDataList = new ArrayList<String[]>();
//        	List<User> updateCardIdList = new ArrayList<User>();
//
//        	// Ԥ����
//        	String regex = "^[A-Za-z0-9]+$";
//            for (int i = 1; i < dataList.size(); i++) {
//            	User user = new User();
//            	String[] data = null;
//            	String cardId = "";
//            	String account = "";
//
//            	data = dataList.get(i);
//                if (data.length < 2) {
//                	result.add(new String[] {data[0], "null", "�˺Ż򿨺Ų���Ϊ��"});
//                    continue;
//                }
//
//                account = data[0];
//            	cardId = data[1];
//                if (!cardId.matches(regex)) {
//                	result.add(new String[] {account, cardId, "���Ÿ�ʽ����ȷ"});
//                	continue;
//                }
//
//                boolean isExistAccount = accountList.contains(account);
//
//                if (!isExistAccount) {
//                	Mylog.info("No." + i + " isExistAccount:" + isExistAccount);
//                	result.add(new String[] {account, cardId, "���˺Ų�����"});
//                	continue;
//                } else {
//                	accountList.remove(account);
//                }
//
//                user.setAccount(account);
//                user.setCardId(cardId);
//                updateCardIdList.add(user);
//            }
//            Mylog.info("Ԥ�������");
//
//            // ��������
//            SqlMapper sqlMapper =  Myspring.getDBBean();
//            List<User> list = new ArrayList<User>();
//            int updateResult = 0;
//            boolean totalUpdateFlag = true;
//
//            int maxValue = 2000;	// 2000
//            int size = updateCardIdList.size();
//            int total = size / maxValue;
//            if (size % maxValue != 0) {
//                total += 1;
//            }
//
//            for (int i = 0; i < total; i++) {
//                if (i == total - 1) {
//                    maxValue = size - (i * maxValue);
//                }
//                list = updateCardIdList.subList(0, maxValue);
//
//                try {
//                	updateResult = sqlMapper.updateUserForCardIdList(list);
//                	Mylog.info("updateResult:" + updateResult);
//
//        		}catch(Exception e){
//        			e.printStackTrace();
//        			Mylog.err("��������ʧ��");
//        			Mylog.err(e.getMessage());
//        			Contants.CLIENT_AUTO_IMPORT_DATA_STATUS = 404;
//        			Contants.CLIENT_QUICK_IMPORT_DATA_STATUS = 404;
//        		}
//
//                if (updateResult !=  maxValue) {
//                	totalUpdateFlag = false;
//                	for (int j = 0; j < maxValue; j++) {
//                		result.add(new String[] {list.get(j).getAccount(), list.get(j).getCardId(), "���µ����ݿ�ʧ��"});
//                    }
//                }
//                list.clear();
//            }
//
//            Mylog.info(Thread.currentThread().getName() + "- ���н��������Ϊ��" +  result.size());
//
//            return result;
//        }
//
//    }
//
//}
