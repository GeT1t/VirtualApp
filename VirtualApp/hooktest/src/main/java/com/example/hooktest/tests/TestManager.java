package com.example.hooktest.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.hooktest.tests.custom.Case1;
import com.example.hooktest.tests.custom.Case10_Default_Constructor;
import com.example.hooktest.tests.custom.Case12_MultiCallback;
import com.example.hooktest.tests.custom.Case2;
import com.example.hooktest.tests.custom.Case3;
import com.example.hooktest.tests.custom.Case4;
import com.example.hooktest.tests.custom.Case7;
import com.example.hooktest.tests.custom.Case8_Activity_onCreate;
import com.example.hooktest.tests.custom.Case9_ThreadMonitor;
import com.example.hooktest.tests.custom.CaseManager;
import com.example.hooktest.tests.arguments.ArgStatic0;
import com.example.hooktest.tests.arguments.ArgStatic4;
import com.example.hooktest.tests.arguments.ArgStatic44;
import com.example.hooktest.tests.arguments.ArgStatic444;
import com.example.hooktest.tests.arguments.ArgStatic4444;
import com.example.hooktest.tests.arguments.ArgStatic4448;
import com.example.hooktest.tests.arguments.ArgStatic448;
import com.example.hooktest.tests.arguments.ArgStatic4484;
import com.example.hooktest.tests.arguments.ArgStatic48;
import com.example.hooktest.tests.arguments.ArgStatic484;
import com.example.hooktest.tests.arguments.ArgStatic4844;
import com.example.hooktest.tests.arguments.ArgStatic488;
import com.example.hooktest.tests.arguments.ArgStatic4888;
import com.example.hooktest.tests.arguments.ArgStatic8;
import com.example.hooktest.tests.arguments.ArgStatic84;
import com.example.hooktest.tests.arguments.ArgStatic844;
import com.example.hooktest.tests.arguments.ArgStatic8444;
import com.example.hooktest.tests.arguments.ArgStatic8448;
import com.example.hooktest.tests.arguments.ArgStatic848;
import com.example.hooktest.tests.arguments.ArgStatic8484;
import com.example.hooktest.tests.arguments.ArgStatic8488;
import com.example.hooktest.tests.arguments.ArgStatic88;
import com.example.hooktest.tests.arguments.ArgStatic884;
import com.example.hooktest.tests.arguments.ArgStatic8844;
import com.example.hooktest.tests.arguments.ArgStatic8848;
import com.example.hooktest.tests.arguments.ArgStatic888;
import com.example.hooktest.tests.arguments.ArgStatic8884;
import com.example.hooktest.tests.arguments.ArgStatic8888;
import com.example.hooktest.tests.invoketype.InvokeConstructor;
import com.example.hooktest.tests.returntype.BooleanType;
import com.example.hooktest.tests.returntype.ByteType;
import com.example.hooktest.tests.returntype.CharType;
import com.example.hooktest.tests.returntype.CustomType;
import com.example.hooktest.tests.returntype.DoubleType;
import com.example.hooktest.tests.returntype.FloatType;
import com.example.hooktest.tests.returntype.IntType;
import com.example.hooktest.tests.returntype.LongType;
import com.example.hooktest.tests.returntype.ShortType;
import com.example.hooktest.tests.returntype.StringArrayType;
import com.example.hooktest.tests.returntype.StringType;
import com.example.hooktest.tests.returntype.VoidType;

/**
 * Created by weishu on 17/11/13.
 */

public class TestManager {
    private static TestManager sManager = new TestManager();

    private List<TestSuite> suites = new ArrayList<>();

    private TestManager() {
        initAllSuites();
    }

    public static TestManager getInstance() {
        return sManager;
    }

    public void addSuite(TestSuite suite) {
        suites.add(suite);
    }

    public List<TestSuite> getAllSuites() {
        return suites;
    }

    private void initAllSuites() {
        TestSuite returnType = new TestSuite("返回值测试");

        returnType.addCase(new VoidType());
        returnType.addCase(new ByteType());
        returnType.addCase(new ShortType());
        returnType.addCase(new CharType());
        returnType.addCase(new IntType());
        returnType.addCase(new FloatType());
        returnType.addCase(new LongType());
        returnType.addCase(new DoubleType());
        returnType.addCase(new BooleanType());
        returnType.addCase(new StringType());
        returnType.addCase(new StringArrayType());
        returnType.addCase(new CustomType());

        addSuite(returnType);


        TestSuite arguments = new TestSuite("参数测试");

        arguments.addCase(new ArgStatic0());
        arguments.addCase(new ArgStatic4());
        arguments.addCase(new ArgStatic8());

        arguments.addCase(new ArgStatic44());
        arguments.addCase(new ArgStatic48());
        arguments.addCase(new ArgStatic84());
        arguments.addCase(new ArgStatic88());

        arguments.addCase(new ArgStatic444());
        arguments.addCase(new ArgStatic448());
        arguments.addCase(new ArgStatic484());
        arguments.addCase(new ArgStatic844());
        arguments.addCase(new ArgStatic488());
        arguments.addCase(new ArgStatic848());
        arguments.addCase(new ArgStatic884());
        arguments.addCase(new ArgStatic888());

        arguments.addCase(new ArgStatic4444());
        arguments.addCase(new ArgStatic4448());
        arguments.addCase(new ArgStatic4484());
        arguments.addCase(new ArgStatic4844());
        arguments.addCase(new ArgStatic8444());

        arguments.addCase(new ArgStatic8844());
        arguments.addCase(new ArgStatic8484());
        arguments.addCase(new ArgStatic8448());
        arguments.addCase(new ArgStatic8884());
        arguments.addCase(new ArgStatic8848());
        arguments.addCase(new ArgStatic8488());
        arguments.addCase(new ArgStatic4888());
        arguments.addCase(new ArgStatic8888());

        addSuite(arguments);


        TestSuite invokeType = new TestSuite("调用类型");
        invokeType.addCase(new InvokeConstructor());

        addSuite(invokeType);

        TestSuite custom = new TestSuite("自定义");
        CaseManager.getInstance().getCase(Case1.class);
        CaseManager.getInstance().getCase(Case2.class);
        CaseManager.getInstance().getCase(Case3.class);
        CaseManager.getInstance().getCase(Case4.class);
        CaseManager.getInstance().getCase(Case4.class);
//        CaseManager.getInstance().getCase(Case5.class);
//        CaseManager.getInstance().getCase(Case6.class);
        CaseManager.getInstance().getCase(Case7.class);
        CaseManager.getInstance().getCase(Case8_Activity_onCreate.class);
        CaseManager.getInstance().getCase(Case9_ThreadMonitor.class);
        CaseManager.getInstance().getCase(Case10_Default_Constructor.class);
//        CaseManager.getInstance().getCase(Case11_SuspendAll.class);
        CaseManager.getInstance().getCase(Case12_MultiCallback.class);

        final Set<Class<?>> cases = CaseManager.getInstance().getCases();
        for (final Class<?> aCase : cases) {
            custom.addCase(new TestCase(aCase.getSimpleName()) {
                @Override
                public void test() {
                    CaseManager.getInstance().getCase(aCase).hook();
                }

                @Override
                public boolean predicate() {
                    return CaseManager.getInstance().getCase(aCase).validate();
                }
            });
        }
        addSuite(custom);
    }

}