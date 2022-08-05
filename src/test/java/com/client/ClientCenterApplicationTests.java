package com.client;

import com.client.model.domain.Users;
import com.client.service.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
class ClientCenterApplicationTests {


	@Resource
	private UsersService usersService;
	@Test
	private void doInsertUsers(){
		StopWatch stopWatch = new StopWatch();
		final int INSERT_NUM = 10000;
		List<Users> usersList = new ArrayList<>();
		stopWatch.start();
		for (int i = 0; i < INSERT_NUM; i++) {
			Users users = new Users();
			users.setUsername("小丑假用户");
			users.setUserAccount("fakeUser");
			users.setAvatarUrl("https://lulyqqqq.github.io/Gallery/leimu/index/02.png");
			users.setGender(0);
			users.setUserPassword("12345678");
			users.setPhone("123456789");
			users.setEmail("123456789@qq.com");
			users.setUserStatus(0);
			users.setUserRole(0);
			users.setTags("[]");
			users.setProfile("这是自我介绍内容");
			users.setPlanetCode("");
			usersList.add(users);
		}
		//mybatis-plus 批量插入每5000条数据为一组进行插入
		usersService.saveBatch(usersList,5000);
		stopWatch.stop();
		//统计运行这段时间花费的时间 ms
		System.out.println(stopWatch.getTotalTimeMillis());
	}


	//线程设置
	/**
	 * 定义线程池
	 */
	private ExecutorService executorService = new ThreadPoolExecutor(16, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));
	/**
	 * 循环插入用户  耗时：7260ms
	 * 批量插入用户   1000  耗时： 4751ms
	 */
	@Test
	public void doInsertUser() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		final int INSERT_NUM = 1000;
		List<Users> usersList = new ArrayList<>();
		for (int i = 0; i < INSERT_NUM; i++) {
			Users users = new Users();
			users.setUsername("小丑假用户");
			users.setUserAccount("fakeUser");
			users.setAvatarUrl("https://lulyqqqq.github.io/Gallery/leimu/index/02.png");
			users.setGender(0);
			users.setUserPassword("12345678");
			users.setPhone("123456789");
			users.setEmail("123456789@qq.com");
			users.setUserStatus(0);
			users.setUserRole(0);
			users.setTags("[]");
			users.setProfile("这是自我介绍内容");
			users.setPlanetCode("");
			usersList.add(users);
		}

		usersService.saveBatch(usersList,100);
		stopWatch.stop();
		System.out.println( stopWatch.getLastTaskTimeMillis());

	}

	/**
	 * 并发批量插入用户   100000  耗时： 26830ms
	 */
	@Test
	public void doConcurrencyInsertUser() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		final int INSERT_NUM = 100000;
		// 分十组
		int j = 0;
		//批量插入数据的大小
		int batchSize = 5000;
		List<CompletableFuture<Void>> futureList = new ArrayList<>();
		// i 要根据数据量和插入批量来计算需要循环的次数。（鱼皮这里直接取了个值，会有问题,我这里随便写的）
		for (int i = 0; i < INSERT_NUM/batchSize; i++) {
			List<Users> usersList = new ArrayList<>();
			while (true){
				j++;
				Users users = new Users();
				users.setUsername("小丑假用户");
				users.setUserAccount("fakeUser");
				users.setAvatarUrl("https://lulyqqqq.github.io/Gallery/leimu/index/02.png");
				users.setGender(0);
				users.setUserPassword("12345678");
				users.setPhone("123456789");
				users.setEmail("123456789@qq.com");
				users.setUserStatus(0);
				users.setUserRole(0);
				users.setTags("[]");
				users.setProfile("这是自我介绍内容");
				users.setPlanetCode("");
				usersList.add(users);
				if (j % batchSize == 0 ){
					break;
				}
			}
			//异步执行
			CompletableFuture<Void> future = CompletableFuture.runAsync(() ->{
				System.out.println("ThreadName：" + Thread.currentThread().getName());
				usersService.saveBatch(usersList,batchSize);
			},executorService);
			futureList.add(future);
		}
		CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

		stopWatch.stop();
		System.out.println( stopWatch.getLastTaskTimeMillis());

	}

}
