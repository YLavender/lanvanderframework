package lanvander.framework.quartz.springboot.handler.sanner;

import lanvander.framework.elasticjob.java.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

@Slf4j
public class ClassScanner implements ResourceLoaderAware {

  private final List<TypeFilter> includesFilters = new LinkedList<>();

  private final List<TypeFilter> excludesFilters = new LinkedList<>();

  private ResourcePatternResolver resourcePatternResolver =
      new PathMatchingResourcePatternResolver();

  private MetadataReaderFactory metadataReaderFactory =
      new CachingMetadataReaderFactory(this.resourcePatternResolver);

  @SafeVarargs
  public static Set<Class<?>> scan(
      String[] basePackages, Class<? extends Annotation>... annotations) {
    ClassScanner classScanner = new ClassScanner();
    if (CollectionUtils.isNotEmpty(annotations))
      Arrays.stream(annotations)
          .forEach(
              annotation -> {
                classScanner.addIncludeFilter(new AnnotationTypeFilter(annotation));
              });

    Set<Class<?>> classSet = new HashSet<>();
    Arrays.asList(basePackages)
        .forEach(
            basePackage -> {
              classSet.addAll(classScanner.scan(basePackage));
            });

    return classSet;
  }

  @SafeVarargs
  public static Set<Class<?>> scan(String basePackage, Class<? extends Annotation>... annotations) {
    return ClassScanner.scan(
        StringUtils.tokenizeToStringArray(basePackage, ",; \t\n"), annotations);
  }

  public Set<Class<?>> scan(String basePackage) {
    Set<Class<?>> classSet = new HashSet<>();
    try {
      String packageSearchPath =
          ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
              + ClassUtils.convertClassNameToResourcePath(
                  SystemPropertyUtils.resolvePlaceholders(basePackage))
              + "/**/*.class";
      Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
      for (Resource resource : resources) {
        if (resource.isReadable()) {
          MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
          if ((includesFilters.size() == 0 && excludesFilters.size() == 0)
              || matches(metadataReader)) {
            classSet.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
          }
        }
      }
    } catch (IOException ex) {
      log.error("类加载失败, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
      throw new BeanDefinitionStoreException("扫描class路径时发生了IO异常", ex);
    } catch (ClassNotFoundException ex) {
      log.error("未找到的类, 异常原因: {}, 异常信息: {}", ex.getCause(), ex.getMessage());
    }

    return classSet;
  }

  protected boolean matches(MetadataReader metadataReader) throws IOException {
    for (TypeFilter typeFilter : excludesFilters) {
      if (typeFilter.match(metadataReader, this.metadataReaderFactory)) return false;
    }
    for (TypeFilter typeFilter : includesFilters) {
      if (typeFilter.match(metadataReader, this.metadataReaderFactory)) return true;
    }
    return false;
  }

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    this.metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
  }

  public void addIncludeFilter(TypeFilter includeFilter) {
    this.includesFilters.add(includeFilter);
  }

  public void addExcludeFilter(TypeFilter excludeFilter) {
    this.excludesFilters.add(excludeFilter);
  }
}
