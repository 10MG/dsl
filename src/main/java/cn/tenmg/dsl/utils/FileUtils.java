package cn.tenmg.dsl.utils;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * 文件工具类
 * 
 * @author June wjzhao@aliyun.com
 * 
 * @since 1.2.4
 *
 */
public abstract class FileUtils {

	private static final String JAR = "jar";

	private static final boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("windows");

	private FileUtils() {
	}

	/**
	 * 递归扫描指定包及其子包下指定后缀名的文件
	 * 
	 * @param basePackage
	 *            指定的包名
	 * @param suffix
	 *            指定的文件后缀名
	 * @return 扫描到的文件列表
	 * @throws IOException
	 *             I/O异常
	 */
	public static List<String> scanPackage(String basePackage, String suffix) throws IOException {
		basePackage = basePackage.replaceAll("\\.", "/");
		URL url = ClassUtils.getDefaultClassLoader().getResource(basePackage);
		if (url != null) {
			if (url.getProtocol().equals(JAR)) {
				List<String> result = new ArrayList<String>();
				Enumeration<JarEntry> entries = ((JarURLConnection) url.openConnection()).getJarFile().entries();
				while (entries.hasMoreElements()) {
					String name = entries.nextElement().getName();
					if (name.endsWith(suffix)) {
						result.add(name);
					}
				}
				return result;
			} else if (url.getProtocol().equals("file")) {
				String path = url.getPath();
				if (isWindows && path.startsWith("/")) {
					path = path.substring(1);
				}
				return walkFileTree(Paths.get(path),
						Paths.get(path.substring(0, path.lastIndexOf(basePackage))).toString(), suffix);
			}
		}
		return null;
	}

	private static List<String> walkFileTree(Path path, String basePath, String suffix) throws IOException {
		final List<String> result = new ArrayList<String>();
		java.nio.file.Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				if (path.toString().endsWith(suffix)) {
					result.add(path.toString().replace(basePath, "").substring(1).replace("\\", "/"));
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

		});
		return result;
	}

}
