package com.nowcoder;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

//线程写法1 继承 Thread 类，实现 run 函数， 就是一个线程
class MyThread extends Thread {
    private int tid;

    public MyThread(int tid) {
        this.tid = tid;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; ++i) {
                Thread.sleep(1000);
                System.out.println(String.format("%d:%d", tid, i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//队列
class Consumer implements Runnable {
    private BlockingQueue<String> q;

    public Consumer(BlockingQueue<String> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(Thread.currentThread().getName() + ": " + q.take());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Produce implements Runnable {
    private BlockingQueue<String> q;

    public Produce(BlockingQueue<String> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; ++i) {
                Thread.sleep(1000);
                q.put(String.valueOf(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class TestRunnable implements Runnable{
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "线程被调用");
    }
}
public class MultiThreadTests {
    public static void testThread() {
        for (int i = 0; i < 10; i++) {
//            for 循环里调用了 10 个线程
            new MyThread(i).start();
        }

        for (int i = 0; i < 10; ++i) {
            final int finalI = i;
//            线程写法2 内部类新建 runnable，实现 run 方法
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 10; ++j) {
                            Thread.sleep(1000);
                            System.out.println(String.format("T2: %d: %d", finalI, j));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //    新建一个对象 做锁
    private static Object obj = new Object();

    public static void testSynchronized1() {
        synchronized (obj) {
            try {
                for (int j = 0; j < 10; ++j) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T3: %d", j));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized2() {
        synchronized (obj) {
            try {
                for (int j = 0; j < 10; ++j) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T4: %d", j));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //    对象锁
    public static void testSynchronized() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }

    public static void testBlockingQueue() {
        BlockingQueue<String> q = new ArrayBlockingQueue<>(100);
        new Thread(new Produce(q)).start();
        new Thread(new Consumer(q), "Consumer1").start();
        new Thread(new Consumer(q), "Consumer2").start();
    }

    private static ThreadLocal<Integer> threadLocalUserIds = new ThreadLocal<>();
    private static int userId;

    public static void testThreadLocal() {
        for (int i = 0; i < 10; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        threadLocalUserIds.set(finalI);
                        Thread.sleep(1000);
                        System.out.println(" ThreadLocal: " + threadLocalUserIds.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        for (int i = 0; i < 10; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        userId = finalI;
                        Thread.sleep(1000);
                        System.out.println(" UserId: " + userId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //    线程池任务框架
    public static void testExecutor() {
//        单线程模式下，第一个任务未完成，第二个任务阻塞
//        ExecutorService service = Executors.newSingleThreadExecutor();

//        多线程模式下，按照指定的线程数执行多任务
        ExecutorService service = Executors.newFixedThreadPool(2);

        //        提交任务，执行任务；
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; ++i) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor1: " + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; ++i) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor2: " + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.submit(new TestRunnable());

        service.execute(new TestRunnable());


//        之前的任务结束后，关闭服务
        service.shutdown();
//        有疑问？？？ 这里为什么能轮询到？
        while (!service.isTerminated()) {
            try {
                Thread.sleep(1000);
                System.out.println("Wait for termination");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //    原子类
    private static int counter = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    private static void testWithoutAtomic() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; ++j) {
                            counter++;
                            System.out.println(counter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static void testWithAtomic() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; ++j) {
//                            atomicInteger++;
                            System.out.println(atomicInteger.incrementAndGet());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void testAtomic() {
//        testWithoutAtomic();
        testWithAtomic();
    }

//    等待异步结果
    public static void testFuture() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 1;
            }
        });

        service.shutdown();
        try {
//            异步获取返回结果
//            System.out.println(future.get());

//            设置超时
            System.out.println(future.get(100, TimeUnit.MILLISECONDS));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
//        testThread();
//        testSynchronized();
//        testBlockingQueue();
//        testThreadLocal();
        testExecutor();
//        testAtomic();
//        testFuture();
    }

}
