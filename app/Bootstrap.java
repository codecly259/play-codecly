import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap<V> extends Job<V> {
	
	public void doJob() {
		// 检查数据是否为空，为空则加载测试文件内的内容
		if(User.count() == 0){
			Fixtures.loadModels("initial-data.yml");
		}
	}
	
}
