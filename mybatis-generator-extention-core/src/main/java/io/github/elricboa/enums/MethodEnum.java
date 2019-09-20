package io.github.elricboa.enums;

/**
 * @author shentongzhou on 2019-09-20
 */
public enum MethodEnum {

    INSERT(1, "insert"),
    BATCH_INSERT(2, "batchInsert"),

    INSERT_SELECTIVE(3, "insertSelective"),
    BATCH_INSERT_SELECTIVE(4, "batchInsertSelective"),

    UPDATE_BY_PRIMARYKEY(5, "updateByPrimaryKey"),
    BATCH_UPDATE_BY_PRIMARYKEY(6, "batchUpdateByPrimaryKey"),

    UPDATE_BY_PRIMARYKEY_SELECTIVE(7, "updateByPrimaryKeySelective"),
    BATCH_UPDATE_BY_PRIMARYKEY_SELECTIVE(8, "batchUpdateByPrimaryKeySelective"),

    DELETE_BY_PRIMARYKEY(9, "deleteByPrimaryKey"),
    BATCH_DELETE_BY_PRIMARYKEY(10, "batchDeleteByPrimaryKey"),
    DELETE_BY_EXAMPLE(11, "deleteByExample"),

    SELECT_BY_PRIMARYKEY(12, "selectByPrimaryKey"),
    BATCH_SELECT_BY_PRIMARYKEY(13, "batchSelectByPrimaryKey"),

    COUNT_BY_SELECTIVE(14, "countBySelective"),
    COUNT_BY_EXAMPLE(15, "countByExample"),
    SELECT_BY_EXAMPLE(16, "selectByExample"),
    UPDATE_BY_EXAMPLE(17, "updateByExample"),
    UPDATE_BY_EXAMPLE_SELECTIVE(18, "updateByExampleSelective"),
    SELECT_BY_EXAMPLE_WITH_PAGE(19, "selectByExampleWithPage"),
    SELECT_BY_SELECTIVE_WITH_PAGE(20, "selectBySelectiveWithPage"),
    EXAMPLE_WHERE_CLAUSE(21, "Example_Where_Clause"),
    BASE_RESULT_MAP(22, "BaseResultMap"),
    BASE_COLUMN_LIST(23, "Base_Column_List"),
    UPDATE_BY_EXAMPLE_WITH_BLOBS(24, "updateByExampleWithBLOBs"),
    UPDATE_BY_PRIMARYKEY_WITH_BLOBS(25, "updateByPrimaryKeyWithBLOBs"),
    SELECTDEFINITION_BY_EXAMPLE(26, "selectDefinitionByExample"),
    SELECTDEFINITION_BY_EXAMPLE_WITH_PAGE(27, "selectDefinitionByExampleWithPage"),
    SELECT_BY_EXAMPLE_WITH_BLOBS(28, "selectByExampleWithBLOBs");

    MethodEnum(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public static MethodEnum getMethodEnumByName(String name) {
        MethodEnum[] methodEnums = MethodEnum.values();
        for (MethodEnum methodEnum : methodEnums) {
            if (methodEnum.getName().equals(name)) {
                return methodEnum;
            }
        }
        return null;
    }

    private int index;
    private String name;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
