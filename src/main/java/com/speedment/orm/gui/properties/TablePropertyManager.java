/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.speedment.orm.gui.properties;

import com.speedment.orm.config.model.External;
import com.speedment.orm.config.model.aspects.Child;
import com.speedment.orm.config.model.impl.utils.MethodsParser;
import com.speedment.orm.config.model.parameters.DbmsType;
import com.speedment.util.java.JavaLanguage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.control.TreeView;

/**
 *
 * @author Emil Forslund
 */
public class TablePropertyManager {
	
	private final TreeView<Child<?>> tree;
	
	public TablePropertyManager(TreeView<Child<?>> view) {
		tree = view;
	}
	
	public Stream<TableProperty<?>> propertiesFor(List<Child<?>> nodes) {
		
		return nodes.stream().flatMap(node -> {
			System.out.println(node);
			return MethodsParser.streamOfExternal(node.getClass())
				.sorted((m0, m1) -> m0.getName().compareTo(m1.getName()))
				.map(m -> {
					final String javaName;
					if (m.getName().startsWith("is")) {
						javaName = m.getName().substring(2);
					} else {
						javaName = m.getName().substring(3);
					}
					
					final String propertyName = JavaLanguage.toHumanReadable(javaName);
					final External e = MethodsParser.getExternalFor(m, node.getClass());
					
					Class<?> type = m.getReturnType();
					Class<?> innerType = e.type();
					boolean optional = Optional.class.isAssignableFrom(type);

					if (Boolean.class.isAssignableFrom(innerType)) {
						return createBooleanProperty(propertyName, nodes, 
							findGetter(node.getClass(), javaName, optional, innerType), 
							findSetter(node.getClass(), javaName, Boolean.class)
						);
					} else if (String.class.isAssignableFrom(innerType)) {
						if ("Password".equals(propertyName)) {
							return createPasswordProperty(propertyName, nodes, 
								findGetter(node.getClass(), javaName, optional, innerType), 
								findSetter(node.getClass(), javaName, String.class)
							);
						} else {
							return createStringProperty(propertyName, nodes, 
								findGetter(node.getClass(), javaName, optional, innerType), 
								findSetter(node.getClass(), javaName, String.class)
							);
						}
					} else if (Class.class.isAssignableFrom(innerType)) {
						return createClassProperty(propertyName, nodes, 
							findGetter(node.getClass(), javaName, optional, innerType), 
							findSetter(node.getClass(), javaName, Class.class)
						);
					} else if (Number.class.isAssignableFrom(innerType)) {
						return createNumberProperty(propertyName, nodes, 
							findGetter(node.getClass(), javaName, optional, innerType), 
							findSetter(node.getClass(), javaName, (Class<Number>) innerType)
						);
					} else if (DbmsType.class.isAssignableFrom(innerType)) {
						return createDbmsTypeProperty(propertyName, nodes, 
							findGetter(node.getClass(), javaName, optional, innerType), 
							findSetter(node.getClass(), javaName, (Class<DbmsType>) innerType)
						);
					} else if (Enum.class.isAssignableFrom(innerType)) {
						return createEnumProperty(propertyName, nodes, 
							findGetter(node.getClass(), javaName, optional, Enum.class), 
							findSetter(node.getClass(), javaName, (Class<Enum>) innerType),
							(Enum) type.getEnumConstants()[0]
						);
					} else {
						throw new UnsupportedOperationException("Found method '" + m + "' in '" + node.getClass() + "' marked as @External of unsupported type " + type.getName());
					}
				});
		}).map(tp -> (TableProperty<?>) tp).distinct();

	}
	
	private <V> Function<Child<?>, V> findGetter(Class<?> nodeClass, String javaName, boolean optional, Class<?> innerType) {
		final String methodName;
		
		if (Boolean.class.isAssignableFrom(innerType)) {
			methodName = "is" + javaName;
		} else {
			methodName = "get" + javaName;
		}

		try {
			final Method method = nodeClass.getMethod(methodName);
			return c -> {
				try {
					if (optional) {
						return ((Optional<V>) method.invoke(c)).orElse(null);
					} else {
						return (V) method.invoke(c);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					throw new RuntimeException("Could not invoke method '" + methodName + "' in class '" + nodeClass.getName() + "'.");
				}
			};
		} catch (NoSuchMethodException ex) {
			throw new RuntimeException("Could not find @External method '" + methodName + "' in class '" + nodeClass.getName() + "'.");
		}
	}
	
	private <V> BiConsumer<Child<?>, V> findSetter(Class<?> nodeClass, String javaName, Class<V> paramType) {
		final String methodName = "set" + javaName;
		
		try {
			final Method method = nodeClass.getMethod(methodName, paramType);
			return (c, v) -> {
				try {
					method.invoke(c, v);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					throw new RuntimeException("Could not invoke method '" + methodName + "' in class '" + nodeClass.getName() + "'.");
				}
			};
		} catch (NoSuchMethodException ex) {
			throw new RuntimeException("Could not find @External method '" + methodName + "' with param '" + paramType + "' in class '" + nodeClass.getName() + "'.");
		}
	}
	
	private TableProperty<Boolean> createBooleanProperty(String label, List<Child<?>> nodes, Function<Child<?>, Boolean> selector, BiConsumer<Child<?>, Boolean> updater) {
		return createProperty(label, nodes, TableBooleanProperty::new, selector, updater);
	}
	
	private TableProperty<String> createStringProperty(String label, List<Child<?>> nodes, Function<Child<?>, String> selector, BiConsumer<Child<?>, String> updater) {
		return createProperty(label, nodes, TableStringProperty::new, selector, updater);
	}
	
	private TableProperty<String> createPasswordProperty(String label, List<Child<?>> nodes, Function<Child<?>, String> selector, BiConsumer<Child<?>, String> updater) {
		return createProperty(label, nodes, TablePasswordProperty::new, selector, updater);
	}
	
	private TableProperty<Class> createClassProperty(String label, List<Child<?>> nodes, Function<Child<?>, Class> selector, BiConsumer<Child<?>, Class> updater) {
		return createProperty(label, nodes, TableClassProperty::new, selector, updater);
	}
	
	private TableProperty<Number> createNumberProperty(String label, List<Child<?>> nodes, Function<Child<?>, Number> selector, BiConsumer<Child<?>, Number> updater) {
		return createProperty(label, nodes, TableNumberProperty::new, selector, updater);
	}
	
	private TableProperty<DbmsType> createDbmsTypeProperty(String label, List<Child<?>> nodes, Function<Child<?>, DbmsType> selector, BiConsumer<Child<?>, DbmsType> updater) {
		return createProperty(label, nodes, TableDbmsTypeProperty::new, selector, updater);
	}
	
	private <V extends Enum<V>> TableProperty<V> createEnumProperty(String label, List<Child<?>> nodes, Function<Child<?>, V> selector, BiConsumer<Child<?>, V> updater, V defaultValue) {
		return createProperty(label, nodes, TableEnumProperty::new, selector, updater, defaultValue);
	}
	
	private <V> TableProperty<V> createProperty(String label, List<Child<?>> nodes, BiFunction<String, V, TableProperty<V>> initiator, Function<Child<?>, V> selector, BiConsumer<Child<?>, V> updater) {
		return createProperty(label, nodes, initiator, selector, updater, null);
	}
	
	private <V> TableProperty<V> createProperty(String label, List<Child<?>> nodes, BiFunction<String, V, TableProperty<V>> initiator, Function<Child<?>, V> selector, BiConsumer<Child<?>, V> updater, V defaultValue) {
		V option = getOption(nodes, selector);
		
		if (option == null) {
			option = defaultValue;
		}
		
		final TableProperty<V> property = initiator.apply(label, option);
		
		property.valueProperty().addListener((ob, o, newValue) -> {
			System.out.println("Value changed in setting to: " + newValue);
			tree.getSelectionModel().getSelectedItems().stream()
                .map(i -> i.getValue())
                .forEachOrdered(c -> updater.accept(c, newValue));
		});
		
		return property;
	}
	
	private <V> V getOption(List<Child<?>> nodes, Function<Child<?>, V> selector) {
		final List<V> variants = nodes.stream().map(n -> selector.apply(n)).distinct().collect(Collectors.toList());
		if (variants.size() == 1) {
			return variants.get(0);
		} else {
			return null;
		}
	}
}
