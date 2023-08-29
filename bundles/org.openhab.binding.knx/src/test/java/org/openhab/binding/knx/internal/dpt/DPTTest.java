/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.knx.internal.dpt;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;

import tuwien.auto.calimero.dptxlator.DPTXlator2ByteUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlator4ByteFloat;
import tuwien.auto.calimero.dptxlator.DPTXlator4ByteSigned;
import tuwien.auto.calimero.dptxlator.DPTXlator4ByteUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlator64BitSigned;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitSigned;
import tuwien.auto.calimero.dptxlator.DptXlator2ByteSigned;

/**
 *
 * @author Simon Kaufmann - Initial contribution
 *
 */
@NonNullByDefault
class DPTTest {

    @Test
    void testToDPTValueTrailingZeroesStrippedOff() {
        assertEquals("3", ValueEncoder.encode(new DecimalType("3"), "17.001"));
        assertEquals("3", ValueEncoder.encode(new DecimalType("3.0"), "17.001"));
    }

    @Test
    public void testToDPTValueDecimalType() {
        assertEquals("23.1", ValueEncoder.encode(new DecimalType("23.1"), "9.001"));
    }

    @Test
    @SuppressWarnings("null")
    void testToDPT5ValueFromQuantityType() {
        assertEquals("80", ValueEncoder.encode(new QuantityType<>("80 %"), "5.001"));

        assertEquals("180", ValueEncoder.encode(new QuantityType<>("180 °"), "5.003"));
        assertTrue(ValueEncoder.encode(new QuantityType<>("3.14 rad"), "5.003").startsWith("179."));
        assertEquals("80", ValueEncoder.encode(new QuantityType<>("80 %"), "5.004"));
    }

    @Test
    @SuppressWarnings("null")
    void testToDPT7ValueFromQuantityType() {
        assertEquals("1000", ValueEncoder.encode(new QuantityType<>("1000 ms"), "7.002"));
        assertEquals("1000", ValueEncoder.encode(new QuantityType<>("1000 ms"), "7.003"));
        assertEquals("1000", ValueEncoder.encode(new QuantityType<>("1000 ms"), "7.004"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1000 ms"), "7.005"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("60 s"), "7.006"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("60 min"), "7.007"));

        assertEquals("1000", ValueEncoder.encode(new QuantityType<>("1 m"), "7.011"));
        assertEquals("1000", ValueEncoder.encode(new QuantityType<>("1000 mA"), "7.012"));
        assertEquals("1000", ValueEncoder.encode(new QuantityType<>("1000 lx"), "7.013"));

        assertEquals("3000", ValueEncoder.encode(new QuantityType<>("3000 K"), "7.600"));
    }

    @Test
    @SuppressWarnings("null")
    void testToDPT8ValueFromQuantityType() {
        assertEquals("1000", ValueEncoder.encode(new QuantityType<>("1000 ms"), "8.002"));
        assertEquals("1000", ValueEncoder.encode(new QuantityType<>("1000 ms"), "8.003"));
        assertEquals("1000", ValueEncoder.encode(new QuantityType<>("1000 ms"), "8.004"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1000 ms"), "8.005"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("60 s"), "8.006"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("60 min"), "8.007"));

        assertEquals("180", ValueEncoder.encode(new QuantityType<>("180 °"), "8.011"));
        assertEquals("1000", ValueEncoder.encode(new QuantityType<>("1 km"), "8.012"));
    }

    @Test
    @SuppressWarnings("null")
    void testToDPT9ValueFromQuantityType() {
        assertEquals("23.1", ValueEncoder.encode(new QuantityType<>("23.1 °C"), "9.001"));
        assertEquals(5.0,
                Double.parseDouble(Objects.requireNonNull(ValueEncoder.encode(new QuantityType<>("41 °F"), "9.001"))));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("274.15 K"), "9.001"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 K"), "9.002"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1000 mK"), "9.002"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 °C"), "9.002"));
        assertTrue(ValueEncoder.encode(new QuantityType<>("1 °F"), "9.002").startsWith("0.55"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 K/h"), "9.003"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 °C/h"), "9.003"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1000 mK/h"), "9.003"));
        assertEquals("600", ValueEncoder.encode(new QuantityType<>("10 K/min"), "9.003"));
        assertEquals("100", ValueEncoder.encode(new QuantityType<>("100 lx"), "9.004"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 m/s"), "9.005"));
        assertTrue(ValueEncoder.encode(new QuantityType<>("1.94 kn"), "9.005").startsWith("0.99"));
        assertEquals(1.0, Double
                .parseDouble(Objects.requireNonNull(ValueEncoder.encode(new QuantityType<>("3.6 km/h"), "9.005"))));
        assertEquals("456", ValueEncoder.encode(new QuantityType<>("456 Pa"), "9.006"));
        assertEquals("70", ValueEncoder.encode(new QuantityType<>("70 %"), "9.007"));
        assertEquals("8", ValueEncoder.encode(new QuantityType<>("8 ppm"), "9.008"));
        assertEquals("9", ValueEncoder.encode(new QuantityType<>("9 m³/h"), "9.009"));
        assertEquals("10", ValueEncoder.encode(new QuantityType<>("10 s"), "9.010"));
        assertEquals("11", ValueEncoder.encode(new QuantityType<>("0.011 s"), "9.011"));

        assertEquals("20", ValueEncoder.encode(new QuantityType<>("20 mV"), "9.020"));
        assertEquals("20", ValueEncoder.encode(new QuantityType<>("0.02 V"), "9.020"));
        assertEquals("21", ValueEncoder.encode(new QuantityType<>("21 mA"), "9.021"));
        assertEquals("21", ValueEncoder.encode(new QuantityType<>("0.021 A"), "9.021"));
        assertEquals("12", ValueEncoder.encode(new QuantityType<>("12 W/m²"), "9.022"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 K/%"), "9.023"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 °C/%"), "9.023"));
        assertTrue(ValueEncoder.encode(new QuantityType<>("1 °F/%"), "9.023").startsWith("0.55"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 kW"), "9.024"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 l/h"), "9.025"));
        assertEquals("60", ValueEncoder.encode(new QuantityType<>("1 l/min"), "9.025"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 l/m²"), "9.026"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 °F"), "9.027"));
        assertTrue(ValueEncoder.encode(new QuantityType<>("-12 °C"), "9.027").startsWith("10."));
        assertEquals("10", ValueEncoder.encode(new QuantityType<>("10 km/h"), "9.028"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 g/m³"), "9.029"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 µg/m³"), "9.030"));
    }

    @Test
    @SuppressWarnings("null")
    void testToDPT10ValueFromQuantityType() {
        // DateTimeTyype, not QuantityType
        assertEquals("Wed, 17:30:00", ValueEncoder.encode(new DateTimeType("2019-06-12T17:30:00Z"), "10.001"));
    }

    @Test
    @SuppressWarnings("null")
    void testToDPT11ValueFromQuantityType() {
        // DateTimeTyype, not QuantityType
        assertEquals("2019-06-12", ValueEncoder.encode(new DateTimeType("2019-06-12T17:30:00Z"), "11.001"));
    }

    @Test
    @SuppressWarnings("null")
    void testToDPT12ValueFromQuantityType() {
        // 12.001: dimensionless

        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 s"), "12.100"));
        assertEquals("2", ValueEncoder.encode(new QuantityType<>("2 min"), "12.101"));
        assertEquals("3", ValueEncoder.encode(new QuantityType<>("3 h"), "12.102"));

        assertEquals("1000", ValueEncoder.encode(new QuantityType<>("1 m^3"), "12.1200"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 l"), "12.1200"));
        assertEquals("2", ValueEncoder.encode(new QuantityType<>("2 m³"), "12.1201"));
    }

    @Test
    @SuppressWarnings("null")
    void testToDPT13ValueFromQuantityType() {
        // 13.001 dimensionless
        assertEquals("24", ValueEncoder.encode(new QuantityType<>("24 m³/h"), "13.002"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("24 m³/d"), "13.002"));

        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 Wh"), "13.010"));
        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 VAh"), "13.011"));
        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 varh"), "13.012"));
        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 kWh"), "13.013"));
        assertEquals("4.2", ValueEncoder.encode(new QuantityType<>("4200 VAh"), "13.014"));
        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 kvarh"), "13.015"));
        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 MWh"), "13.016"));

        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 s"), "13.100"));

        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 l"), "13.1200"));
        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 m³"), "13.1201"));
    }

    @Test
    @SuppressWarnings("null")
    void testToDPT14ValueFromQuantityType() {
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 m/s²"), "14.000"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 rad/s²"), "14.001"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 J/mol"), "14.002"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 /s"), "14.003"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 mol"), "14.004"));

        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 rad"), "14.006"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 °"), "14.007"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 J*s"), "14.008"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 rad/s"), "14.009"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 m²"), "14.010"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 F"), "14.011"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 C/m²"), "14.012"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 C/m³"), "14.013"));

        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 m²/N"), "14.014"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 S"), "14.015"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 S/m"), "14.016"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 kg/m³"), "14.017"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 C"), "14.018"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 A"), "14.019"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 A/m²"), "14.020"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 C*m"), "14.021"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 C/m²"), "14.022"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 V/m"), "14.023"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 C"), "14.024"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 C/m²"), "14.025"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 C/m²"), "14.026"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 V"), "14.027"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 V"), "14.028"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 A*m²"), "14.029"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 V"), "14.030"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 J"), "14.031"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 N"), "14.032"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 Hz"), "14.033"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 rad/s"), "14.034"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 J/K"), "14.035"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 W"), "14.036"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 J"), "14.037"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 Ohm"), "14.038"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 m"), "14.039"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 J"), "14.040"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 lm*s"), "14.040"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 cd/m²"), "14.041"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 lm"), "14.042"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 cd"), "14.043"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 A/m"), "14.044"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 Wb"), "14.045"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 T"), "14.046"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 A*m²"), "14.047"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 T"), "14.048"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 A/m"), "14.049"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 A"), "14.050"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 kg"), "14.051"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 kg/s"), "14.052"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 N/s"), "14.053"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 rad"), "14.054"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 °"), "14.055"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 W"), "14.056"));
        // 14.057: dimensionless
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 Pa"), "14.058"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 Ohm"), "14.059"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 Ohm"), "14.060"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 Ohm*m"), "14.061"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 H"), "14.062"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 sr"), "14.063"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 W/m²"), "14.064"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 m/s"), "14.065"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 Pa"), "14.066"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 N/m"), "14.067"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 °C"), "14.068"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 K"), "14.069"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 K"), "14.070"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 J/K"), "14.071"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 W/m/K"), "14.072"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 V/K"), "14.073"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 s"), "14.074"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 N*m"), "14.075"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 J"), "14.075"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 m³"), "14.076"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 m³/s"), "14.077"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 N"), "14.078"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 J"), "14.079"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 VA"), "14.080"));

        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 m³/h"), "14.1200"));
        assertEquals("1", ValueEncoder.encode(new QuantityType<>("1 l/s"), "14.1201"));
    }

    @Test
    @SuppressWarnings("null")
    void testToDPT19ValueFromQuantityType() {
        // DateTimeTyype, not QuantityType
        assertEquals("2019-06-12 17:30:00", ValueEncoder.encode(new DateTimeType("2019-06-12T17:30:00Z"), "19.001"));
    }

    @Test
    void testToDPT29ValueFromQuantityType() {
        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 Wh"), "29.010"));
        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 VAh"), "29.011"));
        assertEquals("42", ValueEncoder.encode(new QuantityType<>("42 varh"), "29.012"));
    }

    @Test
    public void dpt232RgbValue() {
        // input data
        byte[] data = new byte[] { 123, 45, 67 };

        // this is the old implementation
        String value = "r:123 g:45 b:67";
        int r = Integer.parseInt(value.split(" ")[0].split(":")[1]);
        int g = Integer.parseInt(value.split(" ")[1].split(":")[1]);
        int b = Integer.parseInt(value.split(" ")[2].split(":")[1]);
        HSBType expected = HSBType.fromRGB(r, g, b);

        assertEquals(expected, ValueDecoder.decode("232.600", data, HSBType.class));
    }

    @Test
    public void dpt232HsbValue() {
        // input data
        byte[] data = new byte[] { 123, 45, 67 };

        HSBType hsbType = (HSBType) ValueDecoder.decode("232.60000", data, HSBType.class);

        Assertions.assertNotNull(hsbType);
        Objects.requireNonNull(hsbType);
        assertEquals(173.6, hsbType.getHue().doubleValue(), 0.1);
        assertEquals(17.6, hsbType.getSaturation().doubleValue(), 0.1);
        assertEquals(26.3, hsbType.getBrightness().doubleValue(), 0.1);

        String encoded = ValueEncoder.encode(hsbType, "232.60000");
        assertNotNull(encoded);
        assertEquals(encoded, "r:" + data[0] + " g:" + data[1] + " b:" + data[2]);
    }

    @Test
    public void dpt252EncoderTest() {
        // input data
        byte[] data = new byte[] { 0x26, 0x2b, 0x31, 0x00, 0x00, 0x0e };
        HSBType hsbType = (HSBType) ValueDecoder.decode("251.600", data, HSBType.class);

        assertNotNull(hsbType);
        assertEquals(207, hsbType.getHue().doubleValue(), 0.5);
        assertEquals(23, hsbType.getSaturation().doubleValue(), 0.5);
        assertEquals(19, hsbType.getBrightness().doubleValue(), 0.5);
    }

    // This test checks all our overrides for units. It allows to detect unnecessary overrides when we
    // update Calimero library
    @Test
    public void unitFixes() {
        // 8bit signed (DPT 6)
        assertEquals(DPTXlator8BitSigned.DPT_PERCENT_V8.getUnit(), Units.PERCENT.getSymbol());

        // two byte unsigned (DPT 7)
        assertNotEquals("", DPTXlator2ByteUnsigned.DPT_VALUE_2_UCOUNT.getUnit()); // counts have no unit
        assertNotEquals(DPTXlator2ByteUnsigned.DPT_TIMEPERIOD_10.getUnit(), "ms"); // according to spec, it is ms
        assertNotEquals(DPTXlator2ByteUnsigned.DPT_TIMEPERIOD_100.getUnit(), "ms"); // according to spec, it is ms

        // two byte signed (DPT 8, DPTXlator is missing in calimero 2.5-M1)
        assertNotEquals("", DptXlator2ByteSigned.DptValueCount.getUnit()); // pulses habe no unit

        // 4 byte unsigned (DPT 12)
        assertNotEquals("", DPTXlator4ByteUnsigned.DPT_VALUE_4_UCOUNT.getUnit()); // counts have no unit

        // 4 byte signed (DPT 13)
        assertNotEquals(DPTXlator4ByteSigned.DPT_REACTIVE_ENERGY.getUnit(), Units.VAR_HOUR.toString());
        assertNotEquals(DPTXlator4ByteSigned.DPT_REACTIVE_ENERGY_KVARH.getUnit(), Units.KILOVAR_HOUR.toString());
        assertNotEquals(DPTXlator4ByteSigned.DPT_APPARENT_ENERGY_KVAH.getUnit(), "kVA*h");
        assertNotEquals(DPTXlator4ByteSigned.DPT_FLOWRATE.getUnit(), Units.CUBICMETRE_PER_HOUR.toString());
        assertNotEquals("", DPTXlator4ByteSigned.DPT_COUNT.getUnit()); // counts have no unit

        // four byte float (DPT 14)
        assertNotEquals(DPTXlator4ByteFloat.DPT_CONDUCTANCE.getUnit(), Units.SIEMENS.toString());
        assertNotEquals(DPTXlator4ByteFloat.DPT_ANGULAR_MOMENTUM.getUnit(),
                Units.JOULE.multiply(Units.SECOND).toString());
        assertNotEquals(DPTXlator4ByteFloat.DPT_ACTIVITY.getUnit(), Units.BECQUEREL.toString());
        assertNotEquals(DPTXlator4ByteFloat.DPT_ELECTRICAL_CONDUCTIVITY.getUnit(),
                Units.SIEMENS.divide(SIUnits.METRE).toString());
        assertNotEquals(DPTXlator4ByteFloat.DPT_TORQUE.getUnit(), Units.NEWTON.multiply(SIUnits.METRE).toString());
        assertNotEquals(DPTXlator4ByteFloat.DPT_RESISTIVITY.getUnit(), Units.OHM.multiply(SIUnits.METRE).toString());
        assertNotEquals(DPTXlator4ByteFloat.DPT_ELECTRIC_DIPOLEMOMENT.getUnit(),
                Units.COULOMB.multiply(SIUnits.METRE).toString());
        assertNotEquals(DPTXlator4ByteFloat.DPT_ELECTRIC_FLUX.getUnit(), Units.VOLT.multiply(SIUnits.METRE).toString());
        assertNotEquals(DPTXlator4ByteFloat.DPT_MAGNETIC_MOMENT.getUnit(),
                Units.AMPERE.multiply(SIUnits.SQUARE_METRE).toString());
        assertNotEquals(DPTXlator4ByteFloat.DPT_ELECTROMAGNETIC_MOMENT.getUnit(),
                Units.AMPERE.multiply(SIUnits.SQUARE_METRE).toString());

        // 64 bit signed (DPT 29)
        assertNotEquals(DPTXlator64BitSigned.DPT_REACTIVE_ENERGY.getUnit(), Units.VAR_HOUR.toString());
    }

    private static Stream<String> unitProvider() {
        return DPTUnits.getAllUnitStrings();
    }

    @ParameterizedTest
    @MethodSource("unitProvider")
    public void unitsValid(String unit) {
        String valueStr = "1 " + unit;
        QuantityType<?> value = new QuantityType<>(valueStr);
        Assertions.assertNotNull(value);
    }

    private static Stream<byte[]> rgbValueProvider() {
        // Returning all combinations is too much. Implementation tries to catch rounding errors
        // but is still deterministic to get reproducible test results.
        return IntStream.range(0, 3 * 256)
                .mapToObj(i -> new byte[] { (byte) (i & 0xff), (byte) ((i / 2) & 0xff), (byte) ((i / 3) & 0xff) });
    }

    @ParameterizedTest
    @MethodSource("rgbValueProvider")
    public void dpt232BackToBackTest(byte[] value) {
        backToBackTest232(value, "232.600");
        backToBackTest232(value, "232.60000");
    }

    private void backToBackTest232(byte[] value, String dpt) {
        // decode will apply transformation from raw bytes to String internally
        HSBType hsb = (HSBType) ValueDecoder.decode(dpt, value, HSBType.class);
        Assertions.assertNotNull(hsb);

        // encoding will return a String in notation defined by Calimero: "r:xxx g:xxx b:xxx"
        String result = ValueEncoder.encode(hsb, dpt);

        // for back to back test, compare String representation
        Assertions.assertEquals("r:" + (value[0] & 0xff) + " g:" + (value[1] & 0xff) + " b:" + (value[2] & 0xff),
                result);
    }

    private static Stream<byte[]> xyYValueProvider() {
        // Returning all combinations is too much. Implementation tries to catch rounding errors
        // but is still deterministic to get reproducible test results.
        //
        // Cannot run make use of full numeric range, as colors cannot be represented in CIE space and
        // back conversion would return different results.
        // Use x: 0.1 .. 0.3, y: 0.3 .. 0.5 to be inside the triangle which can be converted without loss
        return IntStream.range(77, 115).mapToObj(i -> new byte[] { (byte) ((i / 2) & 0xff), (byte) ((i) & 0xff),
                (byte) (i & 0xff), (byte) ((i / 3) & 0xff), (byte) (((i - 50) * 3) & 0xff), 3 });
        // last byte has value 3: c+b valid
    }

    @ParameterizedTest
    @MethodSource("xyYValueProvider")
    public void dpt242BackToBackTest(byte[] value) {
        final String dpt = "242.600";

        // decode will apply transformation from raw bytes to String internally
        HSBType hsb = (HSBType) ValueDecoder.decode(dpt, value, HSBType.class);
        Assertions.assertNotNull(hsb);

        // encoding will return a String in notation defined by Calimero: "(x,xxxx y,yyyy) YY,Y %"
        String result = ValueEncoder.encode(hsb, dpt);

        // for back to back test, compare numerical values to allow tolerances
        double dx = (((value[0] & 0xff) << 8) | (value[1] & 0xff)) / 65535.0;
        double dy = (((value[2] & 0xff) << 8) | (value[3] & 0xff)) / 65535.0;
        double dY = ((double) (value[4] & 0xff)) * 100.0 / 255.0;

        Matcher matcher = ValueDecoder.XYY_PATTERN.matcher(result);
        Assertions.assertTrue(matcher.matches());
        String stringx = matcher.group("x");
        String stringy = matcher.group("y");
        String stringY = matcher.group("Y");
        Assertions.assertNotNull(stringx);
        Assertions.assertNotNull(stringy);
        Assertions.assertNotNull(stringY);
        double rx = Double.parseDouble(stringx.replace(',', '.'));
        double ry = Double.parseDouble(stringy.replace(',', '.'));
        double rY = Double.parseDouble(stringY.replace(',', '.'));

        final double tolerance = 0.001;
        if ((Math.abs(dx - rx) > tolerance) || (Math.abs(dy - ry) > tolerance)
                || (Math.abs(dY - rY) > tolerance * 100)) {
            // print failures in a useful format
            Assertions.assertEquals(String.format("(%.4f %.4f) %.1f %%", dx, dy, dY), result);
        }
    }
}
